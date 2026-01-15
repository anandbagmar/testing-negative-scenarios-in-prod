package com.eot.e2e.negative;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class IOSSimPermissions {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static void info(String msg) {
        System.out.printf("[%s] [IOSSimPermissions] %s%n", LocalDateTime.now().format(TS), msg);
    }

    private static void ok(String msg) {
        System.out.printf("[%s] [IOSSimPermissions] ✅ %s%n", LocalDateTime.now().format(TS), msg);
    }

    private static void warn(String msg) {
        System.out.printf("[%s] [IOSSimPermissions] ⚠️ %s%n", LocalDateTime.now().format(TS), msg);
    }

    private static void err(String msg) {
        System.err.printf("[%s] [IOSSimPermissions] ❌ %s%n", LocalDateTime.now().format(TS), msg);
    }

    public static void bootSimIfNeeded(String udid) {
        info("Ensuring simulator is booted. udid=" + udid);

        // bootstatus exits 0 if booted (or can block until booted)
        try {
            run(false, "xcrun", "simctl", "bootstatus", udid, "-b");
            ok("Simulator is booted: " + udid);
        } catch (RuntimeException e) {
            warn("Simulator not booted yet. Attempting boot...");
            run(false, "xcrun", "simctl", "boot", udid);
            run(false, "xcrun", "simctl", "bootstatus", udid, "-b");
            ok("Simulator booted successfully: " + udid);
        }
    }

    public static void grantLocation(String udid, String bundleId) {
        info("Granting Location permission via simctl privacy");
        info("udid=" + udid);
        info("bundleId=" + bundleId);

        run(false, "xcrun", "simctl", "privacy", udid, "grant", "location", bundleId);
        ok("Granted: location -> " + bundleId);

        // Not always supported / required. Best effort.
        try {
            run(true, "xcrun", "simctl", "privacy", udid, "grant", "location-always", bundleId);
            ok("Granted: location-always -> " + bundleId);
        } catch (RuntimeException ignored) {
            // run(true, ...) already logged warning
        }
    }

    public static void setSimLocation(String udid, double lat, double lon) {
        info(String.format("Setting simulator location to lat=%s lon=%s (udid=%s)", lat, lon, udid));
        run(false, "xcrun", "simctl", "location", udid, "set", lat + "," + lon);
        ok("Simulator location set: " + lat + "," + lon);
    }

    public static void resetPermissions(String udid, String bundleId) {
        info("Resetting permissions for app via simctl privacy reset");
        info("udid=" + udid);
        info("bundleId=" + bundleId);
        run(false, "xcrun", "simctl", "privacy", udid, "reset", "all", bundleId);
        ok("Permissions reset for: " + bundleId);
    }

    private static String run(boolean ignoreFailure, String... cmd) {
        String cmdStr = Arrays.stream(cmd).collect(Collectors.joining(" "));
        info("Running: " + cmdStr);

        try {
            Process p = new ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .start();

            String out;
            try (InputStream is = p.getInputStream()) {
                out = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
            }

            int code = p.waitFor();

            if (!out.isBlank()) {
                info("Output:\n" + out);
            }

            if (code != 0) {
                String msg = "Command failed (exit=" + code + "): " + cmdStr;
                if (ignoreFailure) {
                    warn(msg);
                    return out.trim();
                }
                err(msg);
                throw new RuntimeException(msg + "\n" + out);
            }

            ok("Command succeeded");
            return out.trim();

        } catch (Exception e) {
            String msg = "Failed to run: " + cmdStr + " -> " + e.getMessage();
            if (ignoreFailure) {
                warn(msg);
                return "";
            }
            err(msg);
            throw new RuntimeException(msg, e);
        }
    }
}
