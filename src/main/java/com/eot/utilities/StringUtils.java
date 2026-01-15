package com.eot.utilities;

public class StringUtils {
    public static boolean getEnvBoolean(String envName, boolean defaultValue) {
        String value = System.getenv(envName);

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        value = value.trim().toLowerCase();

        // support common truthy values
        return value.equals("true") || value.equals("1") || value.equals("yes") || value.equals("y");
    }
}
