package com.itrane.boothealth.view;

public class HeaderDef {
    public String[] headers;
    public short[] aligns;
    public int[] widths;

    public HeaderDef(String[] headers, short[] aligns, int[] widths) {
        this.headers = headers;
        this.aligns = aligns;
        this.widths = widths;
    }
}
