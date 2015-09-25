package com.itrane.boothealth;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * アプリケーションで使用する定数、選択リストを設定する。
 */
@Component
@PropertySource("ap_const.properties")
@ConfigurationProperties(prefix = "health")
public class AppConst {
    
    // メールREST URL
    private String mailRestUrl;

    // 測定値：バイタル|血液検査
    private int sokuteiVital;
    private int sokuteiBlood;
    // タイプ名称 (0=バイタル, 1=血液検査)
    private List<String> sokuteiTypes;
    // バイタルページ表示行 (0=バイタル, 1=血液検査)
    private List<Integer> pageRows;
    // dtTable のページ取得URL (0=バイタル, 1=血液検査)
    private List<String> pageUrls;
    // インポート処理のURL (0=バイタル, 1=血液検査)
    private List<String> importUrls;

    // 測定値表示期間
    private int dispPeriod;

    // 選択リスト（バイタルタイプ）
    private List<String> vitalTypes;
    // 選択リスト（血液検査タイプ）
    private List<String> bloodTestTypes;

    // 選択リスト（性別）
    private List<String> genders;

    // 選択リスト（血液型）
    private List<String> bloodTypes;

    // 選択リスト（Rh型）
    private List<String> rhTypes;

    // 選択リスト（既往症）
    private List<String> pastIllnesses;

    // フォーマット
    private String dateFormat;
    private String timeFormat;

    // vodList  ページナビゲーションボタンの有効、無効
    private String navibtnEnable;
    private String navibtnDisable;

    public int defaultType() {
        return getSokuteiVital();
    }

    public List<String> vmTypes(int type) {
        return type == 0 ? vitalTypes : bloodTestTypes;
    }

    public String pageUrl(int type) {
        return pageUrls.get(type);
    }

    public String importUrl(int type) {
        return importUrls.get(type);
    }

    public int pageRows(int type) {
        return pageRows.get(type);
    }

    // Getter & Setter
    public int getSokuteiVital() {
        return sokuteiVital;
    }

    public void setSokuteiVital(int sokuteiVital) {
        this.sokuteiVital = sokuteiVital;
    }

    public int getSokuteiBlood() {
        return sokuteiBlood;
    }

    public void setSokuteiBlood(int sokuteiBlood) {
        this.sokuteiBlood = sokuteiBlood;
    }

    public int getDispPeriod() {
        return dispPeriod;
    }

    public void setDispPeriod(int dispPeriod) {
        this.dispPeriod = dispPeriod;
    }

    public List<Integer> getPageRows() {
        return pageRows;
    }

    public void setPageRows(List<Integer> pageRows) {
        this.pageRows = pageRows;
    }

    public List<String> getSokuteiTypes() {
        return sokuteiTypes;
    }

    public void setSokuteiTypes(List<String> sokuteiTypes) {
        this.sokuteiTypes = sokuteiTypes;
    }

    public List<String> getVitalTypes() {
        return vitalTypes;
    }

    public void setVitalTypes(List<String> vitalTypes) {
        this.vitalTypes = vitalTypes;
    }

    public List<String> getBloodTestTypes() {
        return bloodTestTypes;
    }

    public void setBloodTestTypes(List<String> bloodTestTypes) {
        this.bloodTestTypes = bloodTestTypes;
    }

    public List<String> getGenders() {
        return genders;
    }

    public void setGenders(List<String> genders) {
        this.genders = genders;
    }

    public List<String> getBloodTypes() {
        return bloodTypes;
    }

    public void setBloodTypes(List<String> bloodTypes) {
        this.bloodTypes = bloodTypes;
    }

    public List<String> getRhTypes() {
        return rhTypes;
    }

    public void setRhTypes(List<String> rhTypes) {
        this.rhTypes = rhTypes;
    }

    public List<String> getPastIllnesses() {
        return pastIllnesses;
    }

    public void setPastIllnesses(List<String> pastIllnesses) {
        this.pastIllnesses = pastIllnesses;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getNavibtnEnable() {
        return navibtnEnable;
    }

    public void setNavibtnEnable(String navibtnEnable) {
        this.navibtnEnable = navibtnEnable;
    }

    public String getNavibtnDisable() {
        return navibtnDisable;
    }

    public void setNavibtnDisable(String navibtnDisable) {
        this.navibtnDisable = navibtnDisable;
    }

    public List<String> getPageUrls() {
        return pageUrls;
    }

    public void setPageUrls(List<String> pageUrls) {
        this.pageUrls = pageUrls;
    }

    public List<String> getImportUrls() {
        return importUrls;
    }

    public void setImportUrls(List<String> importUrls) {
        this.importUrls = importUrls;
    }

    public String getMailRestUrl() {
        return mailRestUrl;
    }

    public void setMailRestUrl(String mailRestUrl) {
        this.mailRestUrl = mailRestUrl;
    }
    
}
