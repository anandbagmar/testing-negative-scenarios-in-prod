package com.eot.e2e.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SpecmaticStudioRunner {
    private static final Logger LOGGER = LogManager.getLogger(SpecmaticStudioRunner.class);

    private SpecmaticStudioRunner() {}

    /**
     * Start Specmatic Studio: java -jar <jar> <args...>
     * workingDir determines where the process runs from (your case: <project>/lib).
     */
    public static ManagedProcess start(Path jarPath, List<String> args, Path workingDir) {
        Objects.requireNonNull(jarPath, "jarPath");
        if (args == null) args = List.of();

        try {
            List<String> cmd = new ArrayList<>();
            cmd.add("java");
            cmd.add("-jar");
            cmd.add(jarPath.toString());  // may be relative or absolute
            cmd.addAll(args);

            LOGGER.info("[SpecmaticStudio] WorkingDir: {}", workingDir == null ? "<default>" : workingDir.toAbsolutePath());
            LOGGER.info("[SpecmaticStudio] Starting: {}", String.join(" ", cmd));

            ProcessBuilder pb = new ProcessBuilder(cmd);
            if (workingDir != null) {
                pb.directory(workingDir.toFile());
            }

            pb.redirectErrorStream(true);
            Process p = pb.start();

            ManagedProcess mp = new ManagedProcess(p, "SpecmaticStudio");
            LOGGER.info("[SpecmaticStudio] Started pid={}", mp.pid());

            streamLogs(p);
            return mp;
        } catch (Exception e) {
            throw new RuntimeException("Failed to start Specmatic Studio", e);
        }
    }

    private static void streamLogs(Process process) {
        Thread t = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    LOGGER.info("[SpecmaticStudio] {}", line);
                }
            } catch (Exception ignored) {}
        }, "specmatic-studio-logs");

        t.setDaemon(true);
        t.start();
    }
}
