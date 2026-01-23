package com.eot.e2e.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * java -jar ../temp/specmatic-studio-<latest>.jar --specs-dir=proxy_recording_examples proxy
     */
    public static void startProxyMode(Path projectDir) {
        Objects.requireNonNull(projectDir, "projectDir");

        Path specmaticStubDir = projectDir.resolve("src/test/resources/specmatic");
        Path specmaticStudioJarDir = JarDownloader.downloadLatestIfMissing(projectDir);
        Path jarRelative = specmaticStubDir.relativize(specmaticStudioJarDir);

        List<String> args = List.of(
                "--specs-dir=proxy_recording_examples",
                "proxy"
        );

        startOnce(jarRelative, args, specmaticStubDir);
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

    public static boolean isRunning() {
        return proc != null && proc.isAlive();
    }

    public static long pid() {
        return proc == null ? -1 : proc.pid();
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
}
