package com.gastonnicora.trips.utils;

/**
 * Clase utilitaria para identificar el tipo de dispositivo a partir del
 * User-Agent de una solicitud HTTP.
 *
 * <p>
 * Actualmente distingue entre dispositivos Android, iOS y web según el
 * contenido del User-Agent recibido.
 * </p>
 *
 * <ul>
 * <li>Android: si el User-Agent contiene {@code okhttp}, {@code retrofit} o
 * {@code android}.</li>
 * <li>iOS: si el User-Agent contiene {@code iphone} o {@code ios}.</li>
 * <li>Web: por defecto, cuando no coincide con ninguno de los casos
 * anteriores.</li>
 * </ul>
 */
public class UserAgent {

    /**
     * Determina el tipo de dispositivo a partir del User-Agent.
     *
     * <p>
     * La identificación se realiza de forma independiente de mayúsculas y
     * minúsculas mediante la conversión del User-Agent recibido a minúsculas.
     * </p>
     *
     * @param userAgent String con el User-Agent de la solicitud HTTP.
     *
     * @return {@code "android"} si corresponde a Android, {@code "ios"} si
     * corresponde a iOS, o {@code "web"} en cualquier otro caso,
     * incluyendo cuando el User-Agent es {@code null}.
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