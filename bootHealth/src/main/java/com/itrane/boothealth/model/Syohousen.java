package com.itrane.boothealth.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 処方箋.
 * <p>
 * ユーザーごとに管理する.
 * 薬別に管理すべきだが、簡単のため１服用単位で管理する。
 * </p>
 */
@Entity
@Table(name = "syohousen")
public class Syohousen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id = null;

    @Version
    protected Integer version = 0;

    //用法：フリースタイル。例："プレドニンx1.5, ネオーラルx1" 
    private String youhou;
    //服用時間："HH:mm" の24時形式。例："08:00", "18:30"..
    private String fukuyoujikan;
    //服用日："毎日", "曜日", "yyMMdd" の形式など
    //例："毎日", "月", "月水金", "050823"..
    private String fukuyouBi;
    //服用済みフラグ　例："済み", "", キー
    //キーはメール等から RESTリクエストで服用済みを設定するためのキー
    private String fukuyouSumi;
    
    private UserInfo user;
    
    public Syohousen() {}

    public Syohousen(String youhou, String fukuyoujikan, String fukuyouBi,
            String fukuyouSumi, UserInfo user) {
        super();
        this.youhou = youhou;
        this.fukuyoujikan = fukuyoujikan;
        this.fukuyouBi = fukuyouBi;
        this.fukuyouSumi = fukuyouSumi;
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

    public String getYouhou() {
        return youhou;
    }

    public void setYouhou(String youhou) {
        this.youhou = youhou;
    }

    public String getFukuyoujikan() {
        return fukuyoujikan;
    }

    public void setFukuyoujikan(String fukuyoujikan) {
        this.fukuyoujikan = fukuyoujikan;
    }

    public String getFukuyouBi() {
        return fukuyouBi;
    }

    public void setFukuyouBi(String fukuyouBi) {
        this.fukuyouBi = fukuyouBi;
    }

    public String getFukuyouSumi() {
        return fukuyouSumi;
    }

    public void setFukuyouSumi(String fukuyouSumi) {
        this.fukuyouSumi = fukuyouSumi;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
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
        Syohousen other = (Syohousen) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Syohousen [id=" + id + ", version=" + version + ", youhou=" + youhou
                + ", fukuyoujikan=" + fukuyoujikan + ", fukuyouBi=" + fukuyouBi
                + ", fukuyouSumi=" + fukuyouSumi + ", user=" + user + "]";
    }
}
