package com.itrane.boothealth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itrane.boothealth.AppChart;
import com.itrane.boothealth.cmd.StatusCmd;
import com.itrane.boothealth.model.UserInfo;

@Controller
public class HomeController {

    //final private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSource ms;
    @Autowired
    private AppChart appChart;

    // messages プロパティ・マップの取得
    // リクエスト時にモデルに constMsg としてセットされる
    @ModelAttribute("constMsg")
    private Map<String, String> getConst() {
        Map<String, String> cmap = new HashMap<>();
        setMap(cmap, "login.title");
        setMap(cmap, "vod.form.title");
        setMap(cmap, "vod.form.welcome");
        return cmap;
    }

    private void setMap(Map<String, String> cmap, String key) {
        cmap.put(key, ms.getMessage(key, null, Locale.JAPAN));
    }

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("user",
                new UserInfo("tosi", "pass", "太田一郎", "おおたいちろう", null));
        return "views/home";
    }

    /**
     * 製品フォームの入力検証（Ajax版）
     * 
     * @param prod
     *            入力された製品
     * @return ステータスコマンド
     */
    @RequestMapping(value = "/saveProduct")
    public @ResponseBody StatusCmd saveProduct(@RequestBody UserInfo user,
            Locale locale) {
        StatusCmd cmd = new StatusCmd(1, 0, "保存成功");
        return cmd;
    }
}
