package com.gastonnicora.trips.utils;

import java.util.Locale;

/**
 * Clase utilitaria para identificar el tipo de cliente a partir del
 * User-Agent de una solicitud HTTP.
 *
 * <p>
 * Los navegadores, incluidos los de Android e iOS, se identifican siempre con
 * el prefijo {@code Mozilla/}. Por eso se consideran clientes web aunque su
 * User-Agent mencione {@code Android} o {@code iPhone}; de lo contrario un
 * Chrome en Android recibiría el refresh token en el cuerpo en lugar de la
 * cookie.
 * </p>
 *
 * <ul>
 * <li>Web: User-Agent vacío, o que empieza con {@code Mozilla/}.</li>
 * <li>Android (app nativa): contiene {@code okhttp}, {@code retrofit},
 * {@code dalvik} o {@code android}.</li>
 * <li>iOS (app nativa): contiene {@code cfnetwork}, {@code darwin} o
 * {@code alamofire}.</li>
 * <li>Web: por defecto, cuando no coincide con ninguno de los casos
 * anteriores.</li>
 * </ul>
 */
public class UserAgent {

    /**
     * Determina el tipo de dispositivo a partir del User-Agent.
     *
     * <p>
     * La identificación no distingue mayúsculas de minúsculas.
     * </p>
     *
     * @param userAgent String con el User-Agent de la solicitud HTTP.
     *
     * @return {@code "android"} o {@code "ios"} para apps nativas, o
     * {@code "web"} en cualquier otro caso, incluyendo cuando el User-Agent es
     * {@code null} o está vacío.
     */
    public static String getDevice(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "web";
        }

        String ua = userAgent.toLowerCase(Locale.ROOT);

        if (ua.startsWith("mozilla/")) {
            return "web";
        }
        if (ua.contains("okhttp") || ua.contains("retrofit") || ua.contains("dalvik") || ua.contains("android")) {
            return "android";
        }
        if (ua.contains("cfnetwork") || ua.contains("darwin") || ua.contains("alamofire")) {
            return "ios";
        }
        return "web";
    }

}