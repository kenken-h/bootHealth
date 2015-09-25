package com.itrane.common.util.model;

public class DtSearch {

    public String value; // 検索文字列
    public boolean regex; // 正規表現 true | false

    @Override
    public String toString() {
        return "DtSearch [value=" + value + ", regex=" + regex + "]";
    }

}
