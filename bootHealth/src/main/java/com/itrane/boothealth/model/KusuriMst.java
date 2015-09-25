package com.itrane.boothealth.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 薬マスター.
 * <p> 
 * ユーザー全員の薬の管理. 
 * 薬については様々な情報があるので詳細はインターネットから名前で検索する.
 * </p>
 */
@Entity
@Table(name = "kusurimst")
public class KusuriMst implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id = null;

    @Version
    protected Integer version = 0;

    /** 薬の名称 */
    @Column(unique = true, length = 50)
    private String name;
    /** 薬の説明 */
    @Column(length = 800)
    private String setumei;
    /** ジェネリック変更前の薬品名 */
    @Column(length = 50)
    private String generic;
    /** １錠あたりの量 (例:25mg) */
    @Column(length = 20)
    private String youryou;

    public KusuriMst(String name, String setumei, String generic, String youryou) {
        super();
        this.name = name;
        this.setumei = setumei;
        this.generic = generic;
        this.youryou = youryou;
    }

    public KusuriMst() {
        this("", "", "", "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSetumei() {
        return setumei;
    }

    public void setSetumei(String setumei) {
        this.setumei = setumei;
    }

    public String getGeneric() {
        return generic;
    }

    public void setGeneric(String generic) {
        this.generic = generic;
    }

    public String getYouryou() {
        return youryou;
    }

    public void setYouryou(String youryou) {
        this.youryou = youryou;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
        KusuriMst other = (KusuriMst) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "KusuriMst [id=" + id + ", version=" + version + ", name="
                + name + ", setumei=" + setumei + ", generic=" + generic
                + ", youryou=" + youryou + "]";
    }

}
