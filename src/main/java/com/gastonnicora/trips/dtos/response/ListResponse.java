package com.gastonnicora.trips.dtos.response;

import java.util.List;

/**
 * DTO genérico para respuestas que retornan listas de datos.
 * <p>
 * Contiene la lista de elementos y el total de elementos.
 * El campo {@code total} se inicializa automáticamente al tamaño de la lista
 * y se actualiza al establecer una nueva lista mediante {@link #setData(List)}.
 * </p>
 *
 * <p>
 * Ejemplo de uso:
 * </p>
 * 
 * <pre>
 * ListResponse&lt;UserDTO&gt; response = new ListResponse&lt;&gt;(userList);
 * int totalUsers = response.getTotal();
 * List&lt;UserDTO&gt; users = response.getData();
 * </pre>
 *
 * @param <T> Tipo de los elementos de la lista
 */
public class ListResponse<T> {

    /** Lista de elementos devueltos */
    private List<T> data;

    /** Total de elementos en la lista */
    private int total = 0;

    /**
     * Constructor que inicializa la lista y calcula automáticamente el total.
     *
     * @param data Lista de elementos
     */
    public ListResponse(List<T> data) {
        this.data = data;
        this.total = data.size();
    }

    /**
     * Obtiene la lista de elementos.
     *
     * @return Lista de elementos
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Establece una nueva lista de elementos y actualiza automáticamente el total.
     *
     * @param data Nueva lista de elementos
     */
    public void setData(List<T> data) {
        this.data = data;
        this.total = data != null ? data.size() : 0;
    }

    /**
     * Obtiene el total de elementos en la lista.
     *
     * @return Total de elementos
     */
    public int getTotal() {
        return total;
    }

    /**
     * Este método recalcula el total según la lista actual.
     * Si se desea establecer un total manualmente, se puede pasar un valor,
     * pero se recomienda dejar que se calcule automáticamente.
     *
     */
    public void setTotal() {
        this.total = this.data != null ? this.data.size() : 0;
    }
}