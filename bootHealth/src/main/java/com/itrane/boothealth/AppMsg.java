package com.itrane.boothealth;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

/**
 * アプリ共通の文字列定数を定義する。
 * <p>
 * 文字列は messages.properties から取得.
 * ビュー(html) からも Thymeleaf #{..} を使って参照.
 * </p>
 */
public class AppMsg {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // -- アプリ一般
    public final String COMPANY_URL = "http://www.itrane.com/";

    // --- page タイトル ----------
    public final String VOD_FORM_TITLE;
    public final String BLD_FORM_TITLE;
    public final String VOD_FORM_WELLCOME;
    public final String USER_FORM_TITLE;
    public final String USER_LIST_TITLE;
    public final String VM_LIST_TITLE;
    public final String BM_LIST_TITLE;
    public final String KM_LIST_TITLE;
    public final String VOD_LIST_TITLE;
    public final String BLD_LIST_TITLE;

    // --- エクスポート ------------
    public final String VMS_HEAD_LABEL;
    public final String BMS_HEAD_LABEL;
    
    // --- インポート --------------
    public final String IMPORT_INVALID_SHEETNAME;
    public final String IMPORT_INVALID_COLNAME;
    public final String IMPORT_INVALID_SHEETNUM;
    public final String IMPORT_DONE;
    public final String IMPORT_FAIL;
    public final String IMPORT_FILE_EMPTY;
    public final String IMPORT_NOT_EXCEL;

    // -- CRUDメッセージ
    public final String MSG_CREATE;
    public final String MSG_UPDATE;
    public final String MSG_DELETE;

    // -- validation error
    public final String VALID_NAME_DUPLICATE;

    public AppMsg(MessageSource ms) {
        // --- page タイトル ----------
        VOD_FORM_TITLE = getMsg("vod.form.title", ms);
        BLD_FORM_TITLE = getMsg("bld.form.title", ms);
        VOD_FORM_WELLCOME = getMsg("vod.form.title", ms);
        USER_FORM_TITLE = getMsg("user.form.title", ms);
        USER_LIST_TITLE = getMsg("user.list.title", ms);
        VM_LIST_TITLE = getMsg("vm.list.title", ms);
        BM_LIST_TITLE = getMsg("bm.list.title", ms);
        KM_LIST_TITLE = getMsg("km.list.title", ms);
        VOD_LIST_TITLE = getMsg("vod.list.title", ms);
        BLD_LIST_TITLE = getMsg("bld.list.title", ms);

        // --- エクスポート ------------
        VMS_HEAD_LABEL = getMsg("vms.head.label", ms);
        BMS_HEAD_LABEL = getMsg("bms.head.label", ms);

        // --- インポート --------------
        IMPORT_INVALID_SHEETNAME = getMsg("import.invalid.sheetname", ms);
        IMPORT_INVALID_COLNAME = getMsg("import.invalid.colname", ms);
        IMPORT_INVALID_SHEETNUM = getMsg("import.invalid.sheetnum", ms);
        IMPORT_DONE = getMsg("import.done", ms);
        IMPORT_FAIL = getMsg("import.fail", ms);
        IMPORT_FILE_EMPTY = getMsg("import.file.empty", ms);
        IMPORT_NOT_EXCEL = getMsg("import.not.excel", ms);

        // -- CRUDメッセージ
        MSG_CREATE = getMsg("crud.msg.create", ms);
        MSG_UPDATE = getMsg("crud.msg.update", ms);
        MSG_DELETE = getMsg("crud.msg.delete", ms);

        // -- validation error
        VALID_NAME_DUPLICATE = getMsg("valid.name.duplicate", ms);
    }
    
    public String headLabel(int type) {
        return type==0 ? VMS_HEAD_LABEL : BMS_HEAD_LABEL;
    }
    public String vmListTitle(int type) {
        return type==0 ? VM_LIST_TITLE : BM_LIST_TITLE;
    }
    public String vodListTitle(int type) {
        return type==0 ? VOD_LIST_TITLE : BLD_LIST_TITLE;
    }

    /**
     * <p>
     * messages.properties から指定されたプロパティの値を取得する。
     * </p>
     * <p>
     * プロパティが存在しない、あるいは対応する値が空の場合はエラー
     * </p>
     * 
     * @param key
     *            プロパティのキー
     * @param ms
     *            メッセージソース
     * @return メッセージ
     */
    private String getMsg(String key, MessageSource ms) {
        Assert.notNull(key, "key が null！");
        String msg = ms.getMessage(key, null, Locale.JAPAN);
        Assert.hasLength(msg, "msg が 空！");
        return msg;
    }
}
