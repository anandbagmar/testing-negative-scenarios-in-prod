package com.eot.e2e.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class ManagedProcess implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(ManagedProcess.class);

    private final Process process;
    private final String displayName;

    public ManagedProcess(Process process, String displayName) {
        this.process = Objects.requireNonNull(process, "process");
        this.displayName = displayName == null ? "process" : displayName;
    }

    public long pid() {
        try {
            return process.pid();
        } catch (Throwable t) {
            return -1;
        }
    }

    public boolean isAlive() {
        return process.isAlive();
    }

    /**
     * Graceful stop first, then force kill.
     */
    public void stop(Duration gracefulWait) {
        if (!process.isAlive()) {
            LOGGER.info("[{}] Already stopped.", displayName);
            return;
        }

        long pid = pid();
        LOGGER.info("[{}] Stopping (pid={}) ...", displayName, pid);

        process.destroy();

        try {
            boolean exited = process.waitFor(gracefulWait.toMillis(), TimeUnit.MILLISECONDS);
            if (exited) {
                LOGGER.info("[{}] Stopped gracefully (pid={}). ExitCode={}", displayName, pid, process.exitValue());
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        LOGGER.warn("[{}] Graceful stop timed out. Forcibly killing (pid={}) ...", displayName, pid);
        process.destroyForcibly();

        try {
            process.waitFor(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        LOGGER.info("[{}] Force killed (pid={}).", displayName, pid);
    }

    @Override
    public void close() {
        stop(Duration.ofSeconds(5));
    }
}
