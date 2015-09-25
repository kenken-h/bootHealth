package com.itrane.boothealth.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * バイタル/血液検査マスター。
 * <p>
 * １日分のバイタル測定値/1回の血液検査　の定義.
 * ユーザーごとに管理する。
 * </p>
 */
@Entity
@Table(name = "vitalmst")
public class VitalMst implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id = null;

    @Version
    protected Integer version = 0;

    @Column(length = 40)
    private String name;
    /* バイタルでは時間、血液検査では説明を入力する */
    private String jikan;
    private Number kijunMin;
    private Number kijunMax;
    /*
     * バイタルでは体温、体重、血圧など。
     * 血液検査では肝機能、腎機能など検査目的を入力
     */
    @Column(length = 20)
    private String type;
    private int junjo;
    /* SOKUTEI_VITAL:1, SOKUTEI_BLOOD:2 */
    private int syubetu;

    private UserInfo user;

    public VitalMst() {
        //
    }

    public VitalMst(String name, String jikan, Number kijunMin, Number kijunMax,
            String type, int junjo, int syubetu, UserInfo user) {
        super();
        this.name = name;
        this.jikan = jikan;
        this.kijunMin = kijunMin;
        this.kijunMax = kijunMax;
        this.type = type;
        this.junjo = junjo;
        this.user = user;
        this.syubetu = syubetu;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false)
    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJikan() {
        return jikan;
    }

    public void setJikan(String jikan) {
        this.jikan = jikan;
    }

    // 基準値最小
    public Number getKijunMin() {
        return kijunMin;
    }

    public void setKijunMin(Number kijunMin) {
        this.kijunMin = kijunMin;
    }

    // 基準値最大
    public Number getKijunMax() {
        return kijunMax;
    }

    public void setKijunMax(Number kijunMax) {
        this.kijunMax = kijunMax;
    }

    // バイタルのタイプ
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // 測定順序
    public int getJunjo() {
        return junjo;
    }

    public void setJunjo(int junjo) {
        this.junjo = junjo;
    }

    public int getSyubetu() {
        return syubetu;
    }

    public void setSyubetu(int syubetu) {
        this.syubetu = syubetu;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VitalMst other = (VitalMst) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "VitalMst [id=" + id + ", version=" + version + ", name=" + name
                + ", jikan=" + jikan + ", kijunMin=" + kijunMin + ", kijunMax="
                + kijunMax + ", type=" + type + ", junjo=" + junjo
                + ", syubetu=" + syubetu + ", user=" + user + "]";
    }

}
