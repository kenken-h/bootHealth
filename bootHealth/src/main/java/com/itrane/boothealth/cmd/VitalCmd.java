package com.itrane.boothealth.cmd;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * vod/vodForm ビューのバイタル測定値入力クラス.
 * vod/bldForm ビューの血液検査結果入力クラス 
 */
public class VitalCmd {

    // Vital
    @Size(max = 20)
    private String name;
    @Size(max = 10)
    private String sokuteiJikan;
    @NotNull
    @Size(min = 1, max = 5)
    private String sokuteiTi;

    // VMS
    private String yoteiJikan;
    private String type;
    private String errMsg;
    private double kijunMin;
    private double kijunMax;

    public VitalCmd(String name, String sokuteiJikan, String sokuteiTi,
            String yoteiJikan, String type, String errMsg, double kijunMin,
            double kijunMax) {
        super();
        this.name = name;
        this.sokuteiJikan = sokuteiJikan;
        this.sokuteiTi = sokuteiTi;
        this.yoteiJikan = yoteiJikan;
        this.type = type;
        this.errMsg = errMsg;
        this.kijunMin = kijunMin;
        this.kijunMax = kijunMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSokuteiJikan() {
        return sokuteiJikan;
    }

    public void setSokuteiJikan(String sokuteiJikan) {
        this.sokuteiJikan = sokuteiJikan;
    }

    public String getSokuteiTi() {
        return sokuteiTi;
    }

    public void setSokuteiTi(String sokuteiTi) {
        this.sokuteiTi = sokuteiTi;
    }

    public String getYoteiJikan() {
        return yoteiJikan;
    }

    public void setYoteiJikan(String yoteiJikan) {
        this.yoteiJikan = yoteiJikan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public double getKijunMin() {
        return kijunMin;
    }

    public void setKijunMin(double kijunMin) {
        this.kijunMin = kijunMin;
    }

    public double getKijunMax() {
        return kijunMax;
    }

    public void setKijunMax(double kijunMax) {
        this.kijunMax = kijunMax;
    }

    @Override
    public String toString() {
        return "VitalCmd [name=" + name + ", sokuteiJikan=" + sokuteiJikan
                + ", sokuteiTi=" + sokuteiTi + ", yoteiJikan=" + yoteiJikan
                + ", type=" + type + ", errMsg=" + errMsg + ", kijunMin="
                + kijunMin + ", kijunMax=" + kijunMax + "]";
    }
}
