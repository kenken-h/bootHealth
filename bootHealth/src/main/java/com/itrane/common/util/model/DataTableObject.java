package com.itrane.common.util.model;

import java.io.Serializable;
import java.util.List;

public class DataTableObject<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> aaData;
    private int sEcho;
    private Long iTotalRecords;
    private Integer iTotalDisplayRecords;

    public DataTableObject(List<T> aaData, int sEcho, Long iTotalRecords,
            Integer iTotalDisplayRecords) {
        super();
        this.aaData = aaData;
        this.sEcho = sEcho;
        this.iTotalRecords = iTotalRecords;
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public List<T> getAaData() {
        return aaData;
    }

    public void setAaData(List<T> aaData) {
        this.aaData = aaData;
    }

    public int getsEcho() {
        return sEcho;
    }

    public void setsEcho(int sEcho) {
        this.sEcho = sEcho;
    }

    public Long getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(Long iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public Integer getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(Integer iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

}