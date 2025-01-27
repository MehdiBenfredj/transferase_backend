package com.mehdi.oauth.utils;

public class StringUtils {
    public static String trimAfterDelimiters(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // Split at the first occurrence of a delimiter and return the part before it
        String[] parts = str.split("[()\\-_,.]", 2);
        return parts[0];
    }
}
