package com.eot.e2e.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JarDownloader {

    private static final String ARTIFACT_DIR =
            "https://repo.specmatic.io/releases/io/specmatic/studio/specmatic-studio/";

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(2);

    // For directory listing fallback (href="1.16.0/" etc.)
    private static final Pattern VERSION_DIR_PATTERN =
            Pattern.compile("href\\s*=\\s*\"(\\d+(?:\\.\\d+){1,3})/\"", Pattern.CASE_INSENSITIVE);

    // For maven-metadata.xml parsing
    private static final Pattern LATEST_PATTERN =
            Pattern.compile("<latest>\\s*([^<\\s]+)\\s*</latest>");
    private static final Pattern RELEASE_PATTERN =
            Pattern.compile("<release>\\s*([^<\\s]+)\\s*</release>");
    private static final Pattern VERSION_PATTERN =
            Pattern.compile("<version>\\s*([^<\\s]+)\\s*</version>");

    private JarDownloader() {
    }

    /**
     * Ensures the latest specmatic-studio jar is present under <projectDir>/temp.
     * Returns the jar path.
     */
    public static Path downloadLatestIfMissing(Path projectDir) {
        try {
            Objects.requireNonNull(projectDir, "projectDir");

            Path tempDir = projectDir.resolve("temp");
            Files.createDirectories(tempDir);

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .build();

            String latestVersion = resolveLatestVersion(client);

            String jarName = "specmatic-studio-" + latestVersion + ".jar";
            Path jarPath = tempDir.resolve(jarName);

            if (Files.exists(jarPath) && Files.size(jarPath) > 0) {
                return jarPath;
            }

            String jarUrl = ARTIFACT_DIR + latestVersion + "/" + jarName;
            downloadToPath(client, jarUrl, jarPath);

            return jarPath;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to download specmatic-studio jar", e);
        }
    }

    private static String resolveLatestVersion(HttpClient client) throws IOException, InterruptedException {
        // 1) Try Maven metadata (preferred)
        String metadataUrl = ARTIFACT_DIR + "maven-metadata.xml";
        Optional<String> metadata = httpGetText(client, metadataUrl);
        if (metadata.isPresent()) {
            String xml = metadata.get();

            // Prefer <release>, then <latest>, else last <version>
            String release = firstMatch(RELEASE_PATTERN, xml);
            if (isNonBlank(release)) {
                return release;
            }

            String latest = firstMatch(LATEST_PATTERN, xml);
            if (isNonBlank(latest)) {
                return latest;
            }

            List<String> versions = allMatches(VERSION_PATTERN, xml);
            String last = lastStableSemver(versions);
            if (isNonBlank(last)) {
                return last;
            }
        }

        // 2) Fallback: parse artifact directory listing
        Optional<String> listing = httpGetText(client, ARTIFACT_DIR);
        if (listing.isPresent()) {
            List<String> versions = allMatches(VERSION_DIR_PATTERN, listing.get());
            String last = lastStableSemver(versions);
            if (isNonBlank(last)) {
                return last;
            }
        }

        throw new IOException("Unable to determine latest specmatic-studio version from: " + ARTIFACT_DIR);
    }

    private static void downloadToPath(HttpClient client, String url, Path dest) throws IOException, InterruptedException {
        Path part = dest.resolveSibling(dest.getFileName().toString() + ".part");

        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .header("User-Agent", "teswiz-specmatic-downloader")
                .build();

        HttpResponse<InputStream> resp = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
        int code = resp.statusCode();
        if (code < 200 || code >= 300) {
            throw new IOException("Failed to download jar. HTTP " + code + " for " + url);
        }

        try (InputStream in = resp.body()) {
            Files.copy(in, part, StandardCopyOption.REPLACE_EXISTING);
        }

        if (!Files.exists(part) || Files.size(part) == 0) {
            Files.deleteIfExists(part);
            throw new IOException("Downloaded jar is empty: " + part);
        }

        try {
            Files.move(part, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(part, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Optional<String> httpGetText(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .header("User-Agent", "teswiz-specmatic-downloader")
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null && !resp.body().isBlank()) {
            return Optional.of(resp.body());
        }
        return Optional.empty();
    }

    private static String firstMatch(Pattern p, String s) {
        Matcher m = p.matcher(s);
        return m.find() ? m.group(1).trim() : null;
    }

    private static List<String> allMatches(Pattern p, String s) {
        List<String> out = new ArrayList<>();
        Matcher m = p.matcher(s);
        while (m.find()) {
            String v = m.group(1).trim();
            if (!v.isEmpty()) {
                out.add(v);
            }
        }
        return out;
    }

    /**
     * Picks the highest "stable" x.y.z(.w) style version.
     * Skips obvious non-numeric qualifiers (e.g., -SNAPSHOT, -RC).
     */
    private static String lastStableSemver(List<String> versions) {
        if (versions == null || versions.isEmpty()) {
            return null;
        }

        return versions.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(v -> v.matches("\\d+(?:\\.\\d+){1,3}"))
                .max(JarDownloader::compareVersions)
                .orElse(null);
    }

    private static int compareVersions(String a, String b) {
        int[] va = toInts(a);
        int[] vb = toInts(b);
        for (int i = 0; i < Math.max(va.length, vb.length); i++) {
            int ai = i < va.length ? va[i] : 0;
            int bi = i < vb.length ? vb[i] : 0;
            if (ai != bi) {
                return Integer.compare(ai, bi);
            }
        }
        return 0;
    }

    private static int[] toInts(String v) {
        String[] parts = v.split("\\.");
        int[] out = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            out[i] = Integer.parseInt(parts[i]);
        }
        return out;
    }

    private static boolean isNonBlank(String s) {
        return s != null && !s.isBlank();
    }
}
