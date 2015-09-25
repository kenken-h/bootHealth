package com.itrane.boothealth.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.itrane.boothealth.AppConst;
import com.itrane.boothealth.model.Syohousen;
import com.itrane.boothealth.repo.SyohousenRepository;

/**
 * 薬服用を通知するタスク.
 * 処方箋の例： {"薬1 x 3, 薬2 x 1.5", "毎日", "", user1}
 */
@Component
public class TakeMedTask {

    private static final Logger log = LoggerFactory.getLogger(TakeMedTask.class);
    private static final String MAIL_FROM = "送信元アドレス";
    private static final String MAIL_TO = "送信先アドレス";

    @Autowired
    private SyohousenRepository repo;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private AppConst def;

    // 製品版 0秒 10分毎 6-21時 毎日 毎月
    // @Scheduled(cron = "0 */10 6-21 * * ?")
    // デモ 0秒 2分毎 6-21時 毎日 毎月
    @Scheduled(cron = "0   */2  6-21   *   *   ?")
    public void noticeTakeMedicine() {
        /*
         * 処方箋データを調べて服用時間を過ぎていて、服用済になってない場合に
         * メッセージと薬の種類、量を通知する。
         * 服用時間：実際の時間ではなく通知のための目安
         */
        // 現在の時間を取得
        DateTime dtNow = DateTime.now();
        log.debug("&&&&&&&&&&& dtNow = " + dtNow.toString("yy/MM/dd HH:mm EE"));

        // 全ユーザーの処方箋データをスキャン
        List<Syohousen> syohouList = repo.findAll();

        // TODO 処方箋データの初期化（1日の始まりに全件済みマークをクリアする）
        // 前提条件：アプリは24時間稼働と仮定する
        // デモでは起動時にダミーの処方箋を作成するので初期化は省略

        // 通知条件に合致する処方箋を抽出
        String jikan = dtNow.toString("HH:mm");
        Map<String, List<Syohousen>> sendMailMap = new HashMap<>();
        for (Syohousen s : syohouList) {
            String key = s.getUser().getName() + "-" + s.getFukuyoujikan();
            if (isSendMail(jikan, s)) {
                setSendMap(sendMailMap, key, s);
            }
        }
        // 抽出された処方箋に対してメールを送信
        for (String key : sendMailMap.keySet()) {
            List<Syohousen> sendList = sendMailMap.get(key);
            StringBuilder sb = new StringBuilder();
            //服用時間が同一の処方箋を一つにまとめる
            for (Syohousen s : sendList) {
                sb.append(s.getYouhou()).append("\n");
                setKey(key, s);
            }

            // ここでメールを送る
            SimpleMailMessage mailMsg = new SimpleMailMessage();
            mailMsg.setFrom(MAIL_FROM);
            mailMsg.setTo(MAIL_TO);
            mailMsg.setSubject(key + ": お薬の時間です");
            mailMsg.setText(sb.toString() + "\n" + "服用済みの場合は次をクリックしてください\n"
                    + def.getMailRestUrl() + key + "\n");
            javaMailSender.send(mailMsg);
        }
    }

    /**
     * 通知条件にマッチした処方箋を時刻単位でまとめる。
     * <p>
     * 毎日の08:00の薬1,2と月水金の08:00の薬3 の通知は１つにまとめた方がいい。
     * </p>
     * 
     * @param sendMap
     * @param key
     *            ユーザー名＋服用時間
     * @param s
     */
    private void setSendMap(Map<String, List<Syohousen>> sendMap, String key,
            Syohousen s) {
        List<Syohousen> syohouList = sendMap.get(key);
        if (syohouList == null) {
            syohouList = new ArrayList<>();
            syohouList.add(s);
            sendMap.put(key, syohouList);
        } else {
            syohouList.add(s);
        }
    }

    /**
     * 通知条件にマッチするか調べる。
     * 
     * @param jikan
     *            服用時間
     * @param s
     *            処方箋
     * @return マッチする場合は true
     */
    private boolean isSendMail(String jikan, Syohousen s) {
        if ("毎日".equals(s.getFukuyouBi())) {
            /*
             * 曜日の判定や日付判定など様々なパターンが考えられるが、
             * デモでは簡単にするため "毎日"判定しかしない
             */
            if (jikan.compareTo(s.getFukuyoujikan()) > 0) {
                // 現在時間が服用時間を過ぎている場合
                return true;
            }
        }
        return false;
    }
    
    /**
     * 処方箋の服用済みキーを設定する
     * @param s
     */
    private Syohousen setKey(String key, Syohousen s) {
        //TODO デモでは何もしない
        s.setFukuyouSumi(key);
        return repo.save(s);
    }

}