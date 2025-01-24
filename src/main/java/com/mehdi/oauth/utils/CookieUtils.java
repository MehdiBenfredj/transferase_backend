package com.mehdi.oauth.utils;

import jakarta.servlet.http.Cookie;
import org.jetbrains.annotations.NotNull;

public class CookieUtils {
    @NotNull
    public static Cookie createCookie(String userInfo, String encodedCookie) {
        Cookie userInfoCookie = new Cookie(userInfo, encodedCookie);
        userInfoCookie.setHttpOnly(true); // Mark the cookie as HttpOnly for better security
        userInfoCookie.setSecure(true); // Set secure flag if you're using HTTPS
        userInfoCookie.setPath("/"); // Set the path for which this cookie is valid
        userInfoCookie.setMaxAge(14 * 24 * 60 * 60); // Cookie expiry time in seconds (7 days)
        return userInfoCookie;
    }
}
