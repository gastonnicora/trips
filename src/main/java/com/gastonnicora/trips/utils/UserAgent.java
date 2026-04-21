package com.gastonnicora.trips.utils;

public class UserAgent {
    public static String getDevice(String userAgent) {
        String deviceType = "web";
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();

            if (userAgent.contains("okhttp") || userAgent.contains("retrofit") || userAgent.contains("android")) {
                deviceType = "android";
            } else if (userAgent.contains("iphone") || userAgent.contains("ios")) {
                deviceType = "ios";
            }
        }
        return deviceType;
    }

}
