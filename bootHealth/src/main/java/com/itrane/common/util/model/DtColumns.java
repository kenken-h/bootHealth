package com.itrane.common.util.model;

import java.util.List;

public class DtColumns {
    public String data; // mData
    public String name; // 列名
    public boolean searchable; // 検索可
    public boolean orderable; // ソート可
    List<DtSearch> search; // サーチ列

    @Override
    public String toString() {
        return "DtColumns [data=" + data + ", name=" + name + ", searchable="
                + searchable + ", orderable=" + orderable + ", search=" + search
                + "]\n";
    }

}
