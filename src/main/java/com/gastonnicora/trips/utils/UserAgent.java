package com.gastonnicora.trips.utils;

/**
 * Clase utilitaria para identificar el tipo de dispositivo a partir del
 * User-Agent de la solicitud HTTP.
 * <p>
 * Actualmente distingue entre:
 * </p>
 * <ul>
 * <li>Android: si el User-Agent contiene "okhttp", "retrofit" o "android"</li>
 * <li>iOS: si el User-Agent contiene "iphone" o "ios"</li>
 * <li>Web: por defecto, si no coincide con ninguno de los anteriores</li>
 * </ul>
 *
 */
public class UserAgent {

    /**
     * Determina el tipo de dispositivo a partir del User-Agent.
     *
     * @param userAgent String con el User-Agent de la solicitud HTTP.
     * @return "android", "ios" o "web" según corresponda.
     */
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
