package com.itrane.boothealth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    /**
     * 処方箋に、服用済みを設定する。
     * <p>
     * ユーザーに送ったした薬服用通知メールのリンクをクリックしたときに呼ばれる
     * </p>
     * @param key ターゲットの処方箋を検索するキー
     * @return 結果
     */
    @RequestMapping(value = "/syohousen/{key}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.CREATED)
    public String setFukuyosumi(@PathVariable("key") final String key) {
        //TODO デモでは何もしない
        System.out.println("$$$$$$$$$$$$$$$$$ key=" + key);
        //結果もキーを返すだけ
        return key;
    }
}
