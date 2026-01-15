package com.eot.utilities;

public class AndroidCommands {

    private static final String TAG = AndroidCommands.class.getSimpleName();

    public static void adbReverse8080() {
        TestLogger.info(TAG, "Setting up adb reverse");
        ShellUtils.run("adb", "reverse", "tcp:8080", "tcp:8080");
    }
}
