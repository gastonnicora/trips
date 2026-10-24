package com.gastonnicora.trips.enums;

/**
 * Enumeración que define el tipo de una ruta.
 *
 * <p>
 * Una ruta puede ser un recorrido de tipo {@link #TOUR} o un servicio a
 * domicilio ({@link #DOMICILE}).
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
public enum TypeTrip {
    /**
     * Ruta de tipo tour.
     */
    TOUR,

    /**
     * Ruta de servicio a domicilio.
     */
    DOMICILE
}
