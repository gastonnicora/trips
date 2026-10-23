package com.gastonnicora.trips.enums;

/**
 * Enumeración que define la forma en que se calcula el precio de un pasaje en
 * una ruta.
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-19
 */
public enum PricingType {

    /**
     * El precio se calcula según los kilómetros recorridos.
     */
    PER_KM,

    /**
     * El precio se define para cada tramo entre dos puntos de la ruta.
     */
    PER_SECTION
}