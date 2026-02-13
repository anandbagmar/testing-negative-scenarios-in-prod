package com.eot.e2e.tools;

import com.znsio.teswiz.tools.FileUtils;
import com.znsio.teswiz.tools.OsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SpecmaticStudioManager {
    private static final Logger LOGGER = LogManager.getLogger(SpecmaticStudioManager.class);

    private static volatile ManagedProcess proc;
    private static final AtomicBoolean shutdownHookRegistered = new AtomicBoolean(false);

    private SpecmaticStudioManager() {
    }

    /**
     * Convenience wrapper:
     * cd <projectDir>/lib
     * java -jar <gradle_downloaded_dir>/enterprise-all-<version></>.jar proxy
     */
    public static void startProxyMode(Path projectDir) {
        Objects.requireNonNull(projectDir, "projectDir");

        Path specmaticStubDir = projectDir.resolve("src/test/resources/specmatic");
        Path specmaticStudioJarDir = Path.of(getSpecmaticJarPathFromDependencies());

        String destinationName = OsUtils.getUserDirectory() + File.separator + "temp" + File.separator + specmaticStudioJarDir.getFileName();
        LOGGER.info("Copying '" + specmaticStudioJarDir + "' to: '" + destinationName + "'");
        FileUtils.copyFile(specmaticStudioJarDir.toFile(), new File(destinationName));
        List<String> args = List.of("proxy");

        startOnce(specmaticStudioJarDir, args, specmaticStubDir);
    }

    /**
     * Starts Specmatic Studio if it's not already running.
     * jarPath can be relative or absolute (relative preferred when using workingDir).
     */
    public static synchronized void startOnce(Path jarPath, List<String> args, Path workingDir) {
        Objects.requireNonNull(jarPath, "jarPath");

        registerShutdownHookOnce();

        if (proc != null && proc.isAlive()) {
            LOGGER.info("[SpecmaticStudioManager] Specmatic Studio already running. pid={}", proc.pid());
            return;
        }

        LOGGER.info("[SpecmaticStudioManager] Starting Specmatic Studio...");
        proc = SpecmaticStudioRunner.start(jarPath, args, workingDir);
        LOGGER.info("[SpecmaticStudioManager] Started. pid={}", proc.pid());
    }

    public static synchronized void stopIfRunning() {
        if (proc == null) {
            LOGGER.info("[SpecmaticStudioManager] stopIfRunning called, but no process was started.");
            return;
        }

        if (!proc.isAlive()) {
            LOGGER.info("[SpecmaticStudioManager] Process already stopped. pid={}", proc.pid());
            proc = null;
            return;
        }

        LOGGER.info("[SpecmaticStudioManager] Stopping Specmatic Studio pid={} ...", proc.pid());
        proc.stop(Duration.ofSeconds(5));
        proc = null;
        LOGGER.info("[SpecmaticStudioManager] Stopped.");
    }

    private static void registerShutdownHookOnce() {
        if (shutdownHookRegistered.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    stopIfRunning();
                } catch (Exception ignored) {
                }
            }, "specmatic-studio-shutdown-hook"));

            LOGGER.info("[SpecmaticStudioManager] Shutdown hook registered.");
        }
    }

    private static String getSpecmaticJarPathFromDependencies() {
        String jarPath = System.getProperty("specmatic.executableJar");
        if (jarPath == null || jarPath.isBlank()) {
            throw new IllegalStateException("specmatic.executableJar system property not set");
        }
        if (!new File(jarPath).exists()) {
            throw new IllegalStateException("Incorrect path to Specmatic jar file: '%s'".formatted(jarPath));
        }
        return jarPath;
    }
}
