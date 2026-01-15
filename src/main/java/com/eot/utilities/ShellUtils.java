package com.eot.utilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class ShellUtils {

    private static final String TAG = "ShellUtils";

    private ShellUtils() {}

    public enum OSType { WINDOWS, MAC, LINUX, OTHER }

    public record CommandResult(
            List<String> command,
            int exitCode,
            String output,
            boolean timedOut
    ) {
        public boolean isSuccess() {
            return !timedOut && exitCode == 0;
        }
    }

    public static OSType os() {
        String n = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (n.contains("win")) return OSType.WINDOWS;
        if (n.contains("mac")) return OSType.MAC;
        if (n.contains("nix") || n.contains("nux") || n.contains("aix")) return OSType.LINUX;
        return OSType.OTHER;
    }

    /** Best for native binaries (xcrun, adb, gradle, etc.). */
    public static CommandResult run(String... cmd) {
        return run(Duration.ofMinutes(5), cmd);
    }

    public static CommandResult run(Duration timeout, String... cmd) {
        Objects.requireNonNull(cmd, "cmd");
        if (cmd.length == 0) throw new IllegalArgumentException("Empty command");
        return exec(Arrays.asList(cmd), timeout);
    }

    /** Runs a command through the OS shell (bash or powershell). Useful for pipelines. */
    public static CommandResult runInShell(String command) {
        return runInShell(Duration.ofMinutes(5), command, true);
    }

    /**
     * @param preferPowerShell if true on Windows => powershell, else => cmd.exe
     */
    public static CommandResult runInShell(Duration timeout, String command, boolean preferPowerShell) {
        Objects.requireNonNull(command, "command");

        List<String> cmd;
        if (os() == OSType.WINDOWS) {
            if (preferPowerShell) {
                cmd = List.of("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", command);
            } else {
                cmd = List.of("cmd.exe", "/c", command);
            }
        } else {
            cmd = List.of("bash", "-lc", command);
        }

        return exec(cmd, timeout);
    }

    private static CommandResult exec(List<String> cmd, Duration timeout) {
        String pretty = cmd.stream().collect(Collectors.joining(" "));
        TestLogger.info(TAG, "Running: " + pretty);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        try {
            Process p = pb.start();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Thread reader = new Thread(() -> {
                try (InputStream is = p.getInputStream()) {
                    is.transferTo(baos);
                } catch (IOException ioe) {
                    TestLogger.warn(TAG, "Failed to read process output: " + ioe.getMessage());
                }
            }, "shellutils-stream-reader");

            reader.setDaemon(true);
            reader.start();

            boolean finished = p.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            boolean timedOut = !finished;

            if (timedOut) {
                TestLogger.warn(TAG, "Timed out after " + timeout.toSeconds() + "s. Destroying process...");
                p.destroyForcibly();
            }

            int exit = timedOut ? -1 : p.exitValue();

            // give output thread a moment
            try {
                reader.join(2000);
            } catch (InterruptedException ignored) {}

            String output = baos.toString(StandardCharsets.UTF_8).trim();

            if (!output.isBlank()) {
                TestLogger.info(TAG, "Output:\n" + output);
            }

            if (timedOut) {
                TestLogger.warn(TAG, "Exit=-1 (timedOut)");
            } else if (exit == 0) {
                TestLogger.ok(TAG, "Exit=0");
            } else {
                TestLogger.warn(TAG, "Exit=" + exit);
            }

            return new CommandResult(cmd, exit, output, timedOut);

        } catch (Exception e) {
            String msg = "Failed to run: " + pretty + " -> " + e.getMessage();
            TestLogger.err(TAG, msg);
            return new CommandResult(cmd, 1, msg, false);
        }
    }
}
