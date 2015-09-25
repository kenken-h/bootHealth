package com.itrane.boothealth.controller;

import static com.itrane.boothealth.AppUtil.getUser;
import static com.itrane.boothealth.AppUtil.getUserName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import com.itrane.boothealth.AppChart;
import com.itrane.boothealth.AppConst;
import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.cmd.VodListCmd;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;
import com.itrane.boothealth.service.VodService;

/**
 * チャート（バイタル測定値|血液検査結果）を表示する。
 */
@Controller
public class ChartController {

    // private final Logger log = LoggerFactory.getLogger(getClass());

    // %%% リポジトリ %%%%%
    private VodService vodService;
    private VitalMstRepository vmRepo;
    private UserRepository userRepo;

    // %%% アプリ定数 %%%%%
    private AppConst def;

    // %%% チャート設定 %%%%%
    private AppChart cht;

    // %%% DI：リポジトリ、アプリ定数、チャート設定 %%%%%
    @Autowired
    public ChartController(VodService vodService, VitalMstRepository vmRepo,
            UserRepository userRepo, AppConst def, AppChart cht) {
        this.vodService = vodService;
        this.vmRepo = vmRepo;
        this.userRepo = userRepo;
        this.def = def;
        this.cht = cht;
    }

    // バイタルマスター をリクエスト時に Modelにセット
    @ModelAttribute("vms")
    private List<VitalMst> getVitalMst(HttpServletRequest rq) {
        List<VitalMst> vms = new ArrayList<VitalMst>();
        UserInfo user = getUser(userRepo, getUserName(rq));
        if (user != null) {
            vms = vmRepo.findAllByUser(user, def.getSokuteiBlood());
        }
        return vms;
    }

    // =========================
    // 血液検査のチャート
    // =========================
    @RequestMapping(value = "/showBloodChart", method = RequestMethod.POST)
    @ResponseBody
    public Object[][] showBloodChart(@RequestParam("type") String type,
            WebRequest rq) {
        // return getChartData(rq, CHART_HEADERS[type]);
        return getChartData(rq, cht.charHeader(type), def.getSokuteiBlood(), "");
    }

    // =========================
    // バイタルのチャート
    // =========================
    @RequestMapping(value = "/showVitalChart", method = RequestMethod.POST)
    @ResponseBody
    public Object[][] showVitalChart(@RequestParam("type") String type,
            WebRequest rq) {
        return getChartData(rq, cht.charHeader(type), def.getSokuteiVital(),
                cht.chartName(type));
    }

    /*
     * カレントページ（期間）の血液検査データを取得する.
     */
    private List<Vod> getVods(WebRequest request) {
        // セッションからVOD(血液検査）一覧の状態コマンドを取得
        VodListCmd cmd = (VodListCmd) request.getAttribute(AppUtil.MKEY_STS,
                RequestAttributes.SCOPE_SESSION);
        // 取得した血液検査一覧の現在ページの状態からカレントページの血液検査データを取得
        List<Vod> vods = vodService.findPage(cmd.offset,
                def.getPageRows().get(cmd.syubetu), cmd.user, cmd.syubetu);
        return vods;
    }

    /*
     * 指定されたヘッダと血液検査データから指定タイプのチャートデータを作成する.
     */
    private Object[][] getChartData(WebRequest rq, String[] headers, int syubetu,
            String type) {
        List<Vod> vods = getVods(rq);
        List<Object[]> rows = new ArrayList<>();
        // ヘッダ行の作成
        List<Object> headerCols = new ArrayList<>();
        for (String header : headers) {
            headerCols.add(header);
        }
        rows.add(headerCols.toArray(new Object[0]));
        // カレントページの行数分のチャートデータを作成
        for (Vod vod : vods) {
            List<Object> cols = new ArrayList<>();
            cols.add(vod.getSokuteiBi());
            // その行の測定値（有効値は０以上）の合計から有効行｜無効行を判定する
            double sum = 0;
            int cnt = 0;
            List<String> names = Arrays.asList(headers).subList(1, headers.length);
            for (Vital vt : vod.getVitals()) {
                if (syubetu == def.getSokuteiBlood()) {
                    // 1回分の血液検査測定値データから該当するチャート・タイプのデータを抽出
                    // 血液検査の場合 vt.name（検査名） が血液検査の種別を表す
                    for (String name : names) {
                        if (vt.getName().equals(name)) {
                            Double d = new Double(vt.getSokuteiTi());
                            sum += d;
                            cols.add(d);
                        }
                    }
                } else if (syubetu == def.getSokuteiVital()) {
                    //1日分のバイタル測定値データから該当するチャート・タイプのデータを抽出
                    //バイタルの場合、同一タイプ（体温）の時間別の値をまとめてチャートにするので
                    //dt.type を調べて同じタイプのものを抽出する
                    if (vt.getVitalM().getType().equals(type)) {
                        cols.add(new Double(vt.getSokuteiTi()));
                        sum = sum + Double.parseDouble(vt.getSokuteiTi());
                        cnt++;
                    }

                }
            }
            if (type.equals(cht.chartName("Bs"))) {
                // 血糖値チャートに特別な値：全血糖値の平均
                cols.add(new Double(cnt > 0 ? sum / cnt : 0));
            }
            if (sum > 0) {
                // 有効なデータなので行を作成
                rows.add(cols.toArray(new Object[0]));
            }
        }
        return rows.toArray(new Object[0][0]);
    }
}
