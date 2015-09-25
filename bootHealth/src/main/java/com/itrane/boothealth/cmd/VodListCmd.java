package com.itrane.boothealth.cmd;

import java.io.Serializable;
import java.util.List;

import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;

/**
 * vod 一覧ビュー（バイタル：vodList.html / 血液検査:bldList.html）用コマンド.
 */
public class VodListCmd implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 前ページ移動可能か？（"可"｜"不可"）*/
    public String prev;
    /** 次ページ移動可能か？（"可"｜"不可"）*/
    public String next;
    /** バイタルマスター/血液検査マスター */
    public List<VitalMst> vms;
    /** 1ページ分の VOD(バイタル：種別=1/血液検査:種別=2)一覧 */
    public List<Vod> vods;
    /** VODテーブルの取得オフセット値 */
    public int offset;
    /** VODテーブルの対象ユーザー、対象種別の VOD件数 */
    public int total;
    /** 対象種別（種別=1｜種別=2） */
    public int syubetu;
    /** 対象ユーザー */
    public UserInfo user;

    public VodListCmd() {
    }

    /**
     * コンストラク.
     * 
     * @param prev
     * @param next
     * @param vms
     * @param vods
     * @param dt
     */
    public VodListCmd(String prev, String next, List<VitalMst> vms,
            List<Vod> vods, int offset, int total, int syubetu,
            UserInfo user) {
        super();
        this.prev = prev;
        this.next = next;
        this.vms = vms;
        this.vods = vods;
        this.offset = offset;
        this.total = total;
        this.syubetu = syubetu;
        this.user = user;
    }
}
