package com.itrane.boothealth.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

/**
 * ユーザー情報エンティティ.
 * お薬手帳の個人情報.
 */
@Entity
@Table(name = "user")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    // TODO : Sec SHA-256
    // private static final ShaPasswordEncoder spe = new
    // ShaPasswordEncoder(256);

    @Id
    @GeneratedValue
    private Long id = null;

    @Version
    protected Integer version = 0;

    // ログイン情報
    @Column(unique = true)
    @Length(min = 4, max = 12, message = "ユーザー名は{min}から{max}文字の範囲で入力してください。")
    private String name;
    // @Length(min=4, message="パスワードは{min}文字以上の文字列を入力してください。")
    @Transient
    private String ipass;
    private String pass;
    private String roles;

    // 個人情報
    private String simei;
    private String yomi;
    private String seinenGappi;
    private String seiBetu;
    private String telNo;
    private String yubinBango;
    private String toDoFuKen;
    private String siKuTyoSon;
    private String banTi;
    private String tatemono;
    private String ketuekiGata;
    private String rhGata;
    private String yakkyoku;
    private String yakkyokuTel;
    private String yakkyokuFax;
    private String fukuSayouReki;
    private String allergyReki;
    private String byoReki; // 例："病歴１,病歴２,病歴３"
    private String byoRekiSonota;
    private String byoin1Mei;
    private String byoin1Jusyo;
    private String byoin1YoyakuTel;
    private String byoin2Mei;
    private String byoin2Jusyo;
    private String byoin2YoyakuTel;

    @Transient
    private List<String> roleList;
    @Transient
    private List<String> byoRekiList;

    public UserInfo(String name, String ipass, String simei, String yomi,
            String roles) {
        super();
        this.name = name;
        setIpass(ipass);
        this.simei = simei;
        this.yomi = yomi;
        this.roles = roles;
    }

    public UserInfo(String name, String pass) {
        this(name, pass, "", "", "");
    }

    public UserInfo() {
        this("", "", "", "", "");
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getIpass() {
        return ipass;
    }

    public void setIpass(String ipass) {
        this.ipass = ipass;
        if (ipass != null && !ipass.equals("")) {
            // this.pass = spe.encodePassword(ipass, null);
        }
    }

    public String getSimei() {
        return simei;
    }

    public void setSimei(String simei) {
        this.simei = simei;
    }

    public String getYomi() {
        return yomi;
    }

    public void setYomi(String yomi) {
        this.yomi = yomi;
    }

    public String getSeinenGappi() {
        return seinenGappi;
    }

    public void setSeinenGappi(String seinenGappi) {
        this.seinenGappi = seinenGappi;
    }

    public String getSeiBetu() {
        return seiBetu;
    }

    public void setSeiBetu(String seiBetu) {
        this.seiBetu = seiBetu;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getYubinBango() {
        return yubinBango;
    }

    public void setYubinBango(String yubinBango) {
        this.yubinBango = yubinBango;
    }

    public String getToDoFuKen() {
        return toDoFuKen;
    }

    public void setToDoFuKen(String toDoFuKen) {
        this.toDoFuKen = toDoFuKen;
    }

    public String getSiKuTyoSon() {
        return siKuTyoSon;
    }

    public void setSiKuTyoSon(String siKuTyoSon) {
        this.siKuTyoSon = siKuTyoSon;
    }

    public String getBanTi() {
        return banTi;
    }

    public void setBanTi(String banTi) {
        this.banTi = banTi;
    }

    public String getTatemono() {
        return tatemono;
    }

    public void setTatemono(String tatemono) {
        this.tatemono = tatemono;
    }

    public String getKetuekiGata() {
        return ketuekiGata;
    }

    public void setKetuekiGata(String ketuekiGata) {
        this.ketuekiGata = ketuekiGata;
    }

    public String getRhGata() {
        return rhGata;
    }

    public void setRhGata(String rhGata) {
        this.rhGata = rhGata;
    }

    public String getYakkyoku() {
        return yakkyoku;
    }

    public void setYakkyoku(String yakkyoku) {
        this.yakkyoku = yakkyoku;
    }

    public String getYakkyokuTel() {
        return yakkyokuTel;
    }

    public void setYakkyokuTel(String yakkyokuTel) {
        this.yakkyokuTel = yakkyokuTel;
    }

    public String getYakkyokuFax() {
        return yakkyokuFax;
    }

    public void setYakkyokuFax(String yakkyokuFax) {
        this.yakkyokuFax = yakkyokuFax;
    }

    public String getFukuSayouReki() {
        return fukuSayouReki;
    }

    public void setFukuSayouReki(String fukuSayouReki) {
        this.fukuSayouReki = fukuSayouReki;
    }

    public String getAllergyReki() {
        return allergyReki;
    }

    public void setAllergyReki(String allergyReki) {
        this.allergyReki = allergyReki;
    }

    public String getByoReki() {
        return byoReki;
    }

    public void setByoReki(String byoReki) {
        this.byoReki = byoReki;
        if (byoReki != null && byoReki.length() > 0) {
            this.byoRekiList = Arrays.asList(byoReki.split(",", 0));
        }
    }

    public List<String> getByoRekiList() {
        setByoReki(this.byoReki);
        if (this.byoRekiList == null) {
            this.byoRekiList = new ArrayList<String>();
        }
        return byoRekiList;
    }

    public void setByoRekiList(List<String> byoRekiList) {
        this.byoRekiList = byoRekiList;
        if (byoRekiList!=null) {
            this.byoReki = String.join(",", byoRekiList);
        }
    }

    public String getByoRekiSonota() {
        return byoRekiSonota;
    }

    public void setByoRekiSonota(String byoRekiSonota) {
        this.byoRekiSonota = byoRekiSonota;
    }

    public String getByoin1Mei() {
        return byoin1Mei;
    }

    public void setByoin1Mei(String byoin1Mei) {
        this.byoin1Mei = byoin1Mei;
    }

    public String getByoin1Jusyo() {
        return byoin1Jusyo;
    }

    public void setByoin1Jusyo(String byoin1Jusyo) {
        this.byoin1Jusyo = byoin1Jusyo;
    }

    public String getByoin1YoyakuTel() {
        return byoin1YoyakuTel;
    }

    public void setByoin1YoyakuTel(String byoin1YoyakuTel) {
        this.byoin1YoyakuTel = byoin1YoyakuTel;
    }

    public String getByoin2Mei() {
        return byoin2Mei;
    }

    public void setByoin2Mei(String byoin2Mei) {
        this.byoin2Mei = byoin2Mei;
    }

    public String getByoin2Jusyo() {
        return byoin2Jusyo;
    }

    public void setByoin2Jusyo(String byoin2Jusyo) {
        this.byoin2Jusyo = byoin2Jusyo;
    }

    public String getByoin2YoyakuTel() {
        return byoin2YoyakuTel;
    }

    public void setByoin2YoyakuTel(String byoin2YoyakuTel) {
        this.byoin2YoyakuTel = byoin2YoyakuTel;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
        if (roles != null && roles.length() > 0) {
            this.roleList = Arrays.asList(roles.split(",", 0));
        }
    }

    public List<String> getRoleList() {
        this.roleList = new ArrayList<String>();
        if (this.roleList == null) {
            this.roleList = new ArrayList<String>();
        }
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
        if (roleList!=null) {
            this.roles = String.join(",", roleList);
        }
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
        UserInfo other = (UserInfo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserInfo [id=" + id + ", version=" + version + ", name=" + name
                + ", ipass=" + ipass + ", pass=" + pass + ", roles=" + roles
                + ", simei=" + simei + ", yomi=" + yomi + ", seinenGappi="
                + seinenGappi + ", seiBetu=" + seiBetu + ", telNo=" + telNo
                + ", yubinBango=" + yubinBango + ", toDoFuKen=" + toDoFuKen
                + ", siKuTyoSon=" + siKuTyoSon + ", banTi=" + banTi
                + ", tatemono=" + tatemono + ", ketuekiGata=" + ketuekiGata
                + ", rhGata=" + rhGata + ", yakkyoku=" + yakkyoku
                + ", yakkyokuTel=" + yakkyokuTel + ", yakkyokuFax="
                + yakkyokuFax + ", fukuSayouReki=" + fukuSayouReki
                + ", allergyReki=" + allergyReki + ", byoReki=" + byoReki
                + ", byoRekiSonota=" + byoRekiSonota + ", byoin1Mei="
                + byoin1Mei + ", byoin1Jusyo=" + byoin1Jusyo
                + ", byoin1YoyakuTel=" + byoin1YoyakuTel + ", byoin2Mei="
                + byoin2Mei + ", byoin2Jusyo=" + byoin2Jusyo
                + ", byoin2YoyakuTel=" + byoin2YoyakuTel + ", roleList="
                + roleList + ", byoRekiList=" + byoRekiList + "]";
    }

}