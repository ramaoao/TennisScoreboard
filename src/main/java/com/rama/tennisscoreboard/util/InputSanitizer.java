package com.rama.tennisscoreboard.util;

public class InputSanitizer {
    public static String sanitizeName(String name) {
        if (name == null) return null;

        return name.strip().replaceAll("[^\\p{L}\\s\\-]", "");
    }
}
