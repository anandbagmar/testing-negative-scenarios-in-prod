package com.eot.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TestLogger {

    private TestLogger() {
    }

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static String now() {
        return LocalDateTime.now().format(TS);
    }

    public static void info(String tag, String msg) {
        System.out.printf("[%s] [%s] %s%n", now(), tag, msg);
    }

    public static void ok(String tag, String msg) {
        System.out.printf("[%s] [%s] ✅ %s%n", now(), tag, msg);
    }

    public static void warn(String tag, String msg) {
        System.out.printf("[%s] [%s] ⚠️ %s%n", now(), tag, msg);
    }

    public static void err(String tag, String msg) {
        System.err.printf("[%s] [%s] ❌ %s%n", now(), tag, msg);
    }

    /**
     * Convenience method: runtime exception with consistent formatting
     */
    public static RuntimeException fail(String tag, String msg) {
        err(tag, msg);
        return new RuntimeException("[" + tag + "] " + msg);
    }

    /**
     * Convenience method: runtime exception that includes command output
     */
    public static RuntimeException fail(String tag, String msg, String output) {
        err(tag, msg);
        if (output != null && !output.isBlank()) {
            System.err.printf("[%s] [%s] --- output --- %n%s%n", now(), tag, output);
        }
        return new RuntimeException("[" + tag + "] " + msg + (output == null ? "" : "\n" + output));
    }
}

