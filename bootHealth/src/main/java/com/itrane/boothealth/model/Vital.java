package com.itrane.boothealth.model;

import java.io.Serializable;
import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * バイタルクラス.
 * <p>
 * バイタルの測定値、血液検査の結果を保持する.
 * </p>
 */
@Entity
@Table(name = "vital")
public class Vital implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DecimalFormat df = new DecimalFormat("###0.00");

    @Id
    @GeneratedValue
    private Long id = null;

    @Version
    protected Integer version = 0;

    @Column(length = 20)
    @Size(max = 20)
    private String name;

    // 血液検査の説明と共有するので 60文字とする
    @Column(length = 60)
    @Size(max = 60)
    private String sokuteiJikan;

    @NotNull
    @Column(length = 10)
    @Size(min = 1, max = 10)
    private String sokuteiTi;

    private VitalMst vitalM;
    private Vod vod;

    /** コンストラクタ */
    public Vital() {
        super();
    }

    public Vital(String name, String sokuteiJikan, String sokuteiTi, Vod vod,
            VitalMst vitalM) {
        super();
        this.name = name;
        this.sokuteiJikan = sokuteiJikan;
        this.sokuteiTi = sokuteiTi;
        this.vod = vod;
        this.vitalM = vitalM;
    }

    @ManyToOne
    public VitalMst getVitalM() {
        return this.vitalM;
    }

    public void setVitalM(VitalMst vitalM) {
        this.vitalM = vitalM;
    }

    /** 関連するVod の取得 */
    @ManyToOne
    @JoinColumn(name = "VOD_ID", nullable = false)
    public Vod getVod() {
        return vod;
    }

    /** Vod の設定 */
    public void setVod(Vod vod) {
        this.vod = vod;
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
        double d = Double.valueOf(sokuteiTi);
        this.sokuteiTi = df.format(d);
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
        Vital other = (Vital) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Vital [id=" + id + ", version=" + version + ", name=" + name
                + ", sokuteiJikan=" + sokuteiJikan + ", sokuteiTi=" + sokuteiTi
                + ", vod=" + vod + "]";
    }

}
