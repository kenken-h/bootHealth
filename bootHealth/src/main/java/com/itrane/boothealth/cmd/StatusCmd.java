package com.itrane.boothealth.cmd;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * コントローラとビューの間でステータスのやり取りを行う.
 */
public class StatusCmd implements Serializable {

    private static final long serialVersionUID = 1L;

    // 制御ステータス： 1:保存成功 | 9:検証エラー | 0:初期状態
    private int status;
    // 表示ページ：0基準
    private long page;

    // ページ全体のメッセージ
    private String message;
    // フィールドごとのメッセージ
    private Map<String, String> fldErrors;

    private String pageTitle;
    private String pageUrl;
    private String importUrl;
    private int type;

    public StatusCmd() {
        this(0, 0, "");
    }

    public StatusCmd(int status, long page, String message) {
        this.status = status;
        this.page = page;
        this.message = message;
        this.fldErrors = new HashMap<>();
    }

    public StatusCmd(int type, String pageTitle, String pageUrl, String importUrl) {
        this(0, 0, "");
        this.type = type;
        this.pageTitle = pageTitle;
        this.pageUrl = pageUrl;
        this.importUrl = importUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getFldErrors() {
        return fldErrors;
    }

    public void setFldErrors(Map<String, String> fldErrors) {
        this.fldErrors = fldErrors;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getImportUrl() {
        return importUrl;
    }

    public void setImportUrl(String importUrl) {
        this.importUrl = importUrl;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
