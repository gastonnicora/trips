package com.gastonnicora.trips.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO genérico utilizado para representar respuestas de la API que contienen
 * una lista de elementos.
 *
 * <p>
 * Contiene la lista de elementos devueltos y el total de elementos que contiene
 * dicha lista.
 * </p>
 *
 * <p>
 * Cuando la lista recibida es {@code null}, se inicializa una lista vacía y el
 * total se establece en {@code 0}.
 * </p>
 *
 * @param <T> Tipo de los elementos contenidos en la lista.
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-02
 */
@Schema(description = "DTO genérico utilizado para representar una respuesta de la API que contiene una lista de elementos.")
@NoArgsConstructor
public class ListResponse<T> {

    /**
     * Lista de elementos devueltos en la respuesta.
     */
    private List<T> data = new ArrayList<>();

    /**
     * Total de elementos contenidos en la lista.
     */
    private int total = 0;

    /**
     * Constructor que inicializa la lista de elementos y calcula su total.
     *
     * <p>
     * Si {@code data} es {@code null}, se inicializa una lista vacía y el total
     * se establece en {@code 0}.
     * </p>
     *
     * @param data Lista de elementos que contiene la respuesta.
     */
    public ListResponse(List<T> data) {
        this.data = (data != null) ? new ArrayList<>(data) : new ArrayList<>();
        this.total = data != null ? data.size() : 0;
    }

    /**
     * Obtiene la lista de elementos devueltos.
     *
     * @return Lista de elementos de la respuesta.
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Establece la lista de elementos y actualiza el total según su cantidad.
     *
     * <p>
     * Si {@code data} es {@code null}, el total se establece en {@code 0}.
     * </p>
     *
     * @param data Nueva lista de elementos.
     */
    public void setData(List<T> data) {
        this.data = data;
        this.total = data != null ? data.size() : 0;
    }

    /**
     * Obtiene el total de elementos contenidos en la lista.
     *
     * @return Total de elementos de la lista.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Recalcula el total de elementos según la lista actual.
     *
     * <p>
     * Si la lista es {@code null}, el total se establece en {@code 0}.
     * </p>
     */
    public void setTotal() {
        this.total = this.data != null ? this.data.size() : 0;
    }
}
