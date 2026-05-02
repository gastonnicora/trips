package com.gastonnicora.trips.dtos.response;

import java.util.List;

public class ListResponse<T> {

    private List<T> data;
    private int total = 0;

    public ListResponse(List<T> data) {
        this.data = data;
        this.total = data.size();
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = this.data.size();
    }

}
