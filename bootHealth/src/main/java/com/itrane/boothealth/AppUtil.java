package com.itrane.boothealth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.repo.UserRepository;

/**
 * アプリ共通のユーティリティおよび定数定義。
 */
public class AppUtil {

   
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // 定数定義
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // セクリティバージョンでない場合のログイン・ユーザー名を設定する
    public static final String LOGIN_USER_NAME = "user1";
    
    // MAVキー
    public static final String MKEY_STS = "statCmd";
    public static final String MKEY_FRM = "formCmd";
    public static final String MKEY_LST = "listCmd";
    public static final String MKEY_VODS = "vods";

    public static final String MKEY_TITLE = "exportTitle";
    public static final String MKEY_HEAD_LABEL = "exportHeadLabel";
    public static final String MKEY_FILE_NAME = "exportFileName";
    public static final String MKEY_SHEET_NAME = "exportSheetName";

    // --- ビュー名 --------------
    public static final String VIEW_USER_LIST = "views/user/userList";
    public static final String VIEW_USER_FORM = "views/user/userTagForm";
    public static final String VIEW_KM_LIST = "views/kusuriMstList";
    public static final String[] VIEW_VM_LIST = {"views/vitalMstList", "views/bloodMstList"};
    public static final String[] VIEW_VOD_LIST = {"views/vod/vodList", "views/vod/bldList"};
    public static final String[] VIEW_VOD_FORM = {"views/vod/vodForm", "views/vod/bldForm"};

    // --- インポートタイプ -----------
    public static final int IMPORT_USER = 101;
    public static final int IMPORT_VOD_VTL = 111;
    public static final int IMPORT_VOD_BLD = 112;
    public static final int IMPORT_VM = 121;
    public static final int IMPORT_BM = 122;
    public static final int IMPORT_KM = 130;

    // -- PDF エクスポート関連
    public static final String PDF_FONT_NAME =
            "/Library/Fonts/Hiragino Sans GB W3.otf";
    public static final String PDF_PAGE_LABEL = "page ";


    // messages.properties で一元管理
    public static AppMsg msg;
    

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // ログイン・ユーザーの情報
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    /**
     * <p>
     * 指定されたユーザー名のユーザー情報を取得する。
     * 見つからない場合は空のユーザー情報を返す。
     * </p>
     * 
     * @param repo
     *            ユーザーリポジトリ
     * @param userName
     *            ユーザー名
     * @return 取得ユーザーまたは null
     */
    static public UserInfo getUser(UserRepository repo, String userName) {
        // 指定ユーザー名で検索
        List<UserInfo> users = repo.findByName(userName);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    /**
     * <p>
     * ログインユーザー名を取得する.
     * ユーザーログインによるセキュリティ管理を行っていない場合も Spring Security
     * を使ったアプリとコードを共通化するために ダミーのユーザー名 "user1" を返す。
     * </p>
     * 
     * @param rq
     * @return ログインユーザー名
     */
    static public String getUserName(HttpServletRequest rq) {
        String userName = LOGIN_USER_NAME; // デフォルトで設定
        if (rq.getUserPrincipal() != null) {
            userName = rq.getUserPrincipal().getName();
        }
        return userName;
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // EclipseLink 重複エラー判定：
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public static String validDuplicateError(String errMsg) {
        int dupPos = errMsg.indexOf("Duplicate entry");
        int keyPos = errMsg.indexOf("for key");
        int ecdPos = errMsg.indexOf("Error Code");
        if (dupPos > 0) {
            return errMsg.substring(keyPos + 9, ecdPos - 2).toLowerCase();
        }
        return null;
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // EclipseLink 楽観的ロックエラー判定：
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public static String validOptimistickLockError(String errMsg) {
        return null;
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // 乗り換え案内
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    /**
     * <p>
     * 乗換え案内のURLを返す
     * </p>
     * 
     * @param from
     *            出発地 (大森駅)
     * @param to
     *            到着地 (東京駅)
     * @param date
     *            日付 (2010/09/02)
     * @param time
     *            時刻 (15:00)
     * @param type
     *            出発｜到着 (dep|arr)
     * @param express
     *            有料特急の使用 (true|false)
     */
    public static String getTransitURL(String from, String to, String date,
            String time, String type, boolean express) {
        StringBuilder sb = new StringBuilder();
        try {
            String encFrom = URLEncoder.encode(from, "utf-8");
            String encTo = URLEncoder.encode(to, "utf-8");
            String encDate = URLEncoder.encode(date, "utf-8");
            String encTime = URLEncoder.encode(time, "utf-8");

            sb.append("http://www.google.co.jp/maps?ie=UTF8&f=d&dirflg=r")
                    .append("&saddr=").append(encFrom).append("&daddr=").append(encTo)
                    .append("&date=").append(encDate).append("&time=").append(encTime)
                    .append("&ttype=").append(type).append("&sort=time");

            if (!express) {
                // 有料特急を使用しない場合
                sb.append("&noexp=1");
            }
        } catch (UnsupportedEncodingException e1) {
        }
        return sb.toString();
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // JSONユーティリティ
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    /**
     * <p>
     * オブジェクトを JSON 文字列に変換.
     * </p>
     * 
     * @param dt
     * @return
     */
    static public String toJson(Object dt) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(dt);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>
     * Query文字列から指定タイプのインスタンスを取得.
     * </p>
     * 
     * @param type
     * @param qs
     * @return
     */
    static public <T> T fromJson(Class<T> type, String qs)
            throws UnsupportedEncodingException {
        T t = null;
        if (qs != null && qs.length() > 0) {
            String[] ps = URLDecoder.decode(qs, "UTF-8").split("&");
            if (ps.length > 0) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false);
                try {
                    t = mapper.readValue(ps[0], type);
                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return t;
    }
}
