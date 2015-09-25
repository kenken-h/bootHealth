package com.itrane.common.util.model;

public class DtOrder {

    public int column; // ソート列のインデックス（０基準）
    public String dir; // ソートの方向 "asc" | "desc"

    @Override
    public String toString() {
        return "DtOrder [column=" + column + ", dir=" + dir + "]";
    }

}
