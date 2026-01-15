package com.eot.utilities;

public class IOSCommands {

    private static final String TAG = IOSCommands.class.getSimpleName();

    public static void bootSimIfNeeded(String udid) {
        TestLogger.info(TAG, "Ensuring simulator is booted. udid=" + udid);

        ShellUtils.CommandResult bootStatus = ShellUtils.run("xcrun", "simctl", "bootstatus", udid, "-b");
        if (bootStatus.isSuccess()) {
            TestLogger.ok(TAG, "Simulator is booted: " + udid);
            return;
        }

        TestLogger.warn(TAG, "bootstatus failed; attempting boot...");
        ShellUtils.CommandResult boot = ShellUtils.run("xcrun", "simctl", "boot", udid);
        if (!boot.isSuccess()) {
            throw TestLogger.fail(TAG, "Failed to boot simulator: " + udid, boot.output());
        }

        ShellUtils.CommandResult bootStatus2 = ShellUtils.run("xcrun", "simctl", "bootstatus", udid, "-b");
        if (!bootStatus2.isSuccess()) {
            throw TestLogger.fail(TAG, "Simulator did not reach booted state: " + udid, bootStatus2.output());
        }

        TestLogger.ok(TAG, "Simulator booted successfully: " + udid);
    }

    public static void resetPermissions(String udid, String bundleId) {
        TestLogger.info(TAG, "Resetting permissions. udid=" + udid + ", bundleId=" + bundleId);

        ShellUtils.CommandResult r = ShellUtils.run("xcrun", "simctl", "privacy", udid, "reset", "all", bundleId);
        if (!r.isSuccess()) throw TestLogger.fail(TAG, "Failed to reset permissions", r.output());

        TestLogger.ok(TAG, "Permissions reset for: " + bundleId);
    }

    public static void grantLocation(String udid, String bundleId) {
        TestLogger.info(TAG, "Granting location. udid=" + udid + ", bundleId=" + bundleId);

        ShellUtils.CommandResult loc = ShellUtils.run("xcrun", "simctl", "privacy", udid, "grant", "location", bundleId);
        if (!loc.isSuccess()) throw TestLogger.fail(TAG, "Failed to grant 'location'", loc.output());
        TestLogger.ok(TAG, "Granted: location -> " + bundleId);

        // Best effort
        ShellUtils.CommandResult always = ShellUtils.run("xcrun", "simctl", "privacy", udid, "grant", "location-always", bundleId);
        if (always.isSuccess()) {
            TestLogger.ok(TAG, "Granted: location-always -> " + bundleId);
        } else {
            TestLogger.warn(TAG, "location-always not granted (often OK depending on iOS).");
        }
    }

    public static void setSimLocation(String udid, double lat, double lon) {
        TestLogger.info(TAG, "Setting simulator location to " + lat + "," + lon + " (udid=" + udid + ")");

        ShellUtils.CommandResult r = ShellUtils.run("xcrun", "simctl", "location", udid, "set", lat + "," + lon);
        if (!r.isSuccess()) throw TestLogger.fail(TAG, "Failed to set simulator location", r.output());

        TestLogger.ok(TAG, "Simulator location set: " + lat + "," + lon);
    }
}
