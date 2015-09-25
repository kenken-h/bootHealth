package com.itrane.boothealth.controller;

import static com.itrane.boothealth.AppUtil.getUser;
import static com.itrane.boothealth.AppUtil.getUserName;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import com.itrane.boothealth.AppConst;
import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.cmd.VodListCmd;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;
import com.itrane.boothealth.repo.VodRepository;
import com.itrane.boothealth.service.VodService;

/**
 * バイタル一覧/血液検査一覧処理を行う。
 */
@Controller
public class VodController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // %%% リポジトリ、サービス %%%%%
    private VodRepository vodRepo;
    private VodService vodService;
    private VitalMstRepository vmRepo;
    private UserRepository userRepo;

    // %%% アプリ定数 %%%%%
    private AppConst def;

    // %%% DI：リポジトリ、アプリ定数 %%%%%
    @Autowired
    public VodController(VodRepository vodRepo, VodService vodService,
            VitalMstRepository vmRepo, UserRepository userRepo, AppConst def) {
        this.vodRepo = vodRepo;
        this.vodService = vodService;
        this.vmRepo = vmRepo;
        this.userRepo = userRepo;
        this.def = def;
    }

    // --- バイタルマスター をリクエスト時に Modelにセット
    // type==null または 0 のときは バイタルマスター、1のときは血液検査マスター
    @ModelAttribute("vms")
    private List<VitalMst> getVitalMst(HttpServletRequest rq, Integer type) {
        List<VitalMst> vms = new ArrayList<VitalMst>();
        UserInfo user = getUser(userRepo, getUserName(rq));
        if (user != null) {
            vms = vmRepo.findAllByUser(user, type == null ? def.defaultType() : type);
        }
        return vms;
    }

    /**
     * システム日付から過去1ページ分(def.pageRows)のバイタル/血液検査一覧を表示する.
     * <p>
     * データは日付の古いものから順に保存されるので、ページ行数=10、保存件数=25場合:
     * 0ページ: 0- 9 , 1ページ: 10-19, 2ページ: 20-24
     * となるが、実際に欲しいのは最後の10件なので、日付で逆順にソートした
     * 0ページ: (0)24-15, 1ページ: (10)14- 5, 2ページ: (20)4- 0
     * の最初のページを取得する.
     * </p>
     * <p>
     * offset=0, ページ行数=10 から ページ番号 = 0/10 = 0が求められ
     * vodService.findPage により 24-15 が取得される.
     * ページへは古い順に表示したいので取得リストを逆順にする.
     * セッションには offsetと保存件数を保存する
     * </p>
     * 
     * @param model
     * @param rq
     * @param type
     *            取得するデータのタイプ：バイタル=0, 血液検査=1
     * @param vms
     *            バイタルマスター｜血液検査マスター
     * @return
     */
    @RequestMapping(value = "/vodList", method = RequestMethod.GET)
    public String getVodList(Model model, WebRequest rq, Integer type,
            @ModelAttribute("vms") List<VitalMst> vms) {

        // ログインユーザーを取得
        String userName = AppUtil.LOGIN_USER_NAME;
        if (rq.getUserPrincipal() != null) {
            // セキュア版の場合は実際のログインユーザーを取得
            userName = rq.getUserPrincipal().getName();
        }
        UserInfo user = getUser(userRepo, userName);

        // 前ページ移動の初期設定＝不可
        String prev = def.getNavibtnDisable();
        int offset = 0;
        int total = 0;
        int syubetu = type == null ? def.defaultType() : type;
        int pageRows = def.pageRows(syubetu);
        
        // ページ分のデータを取得
        List<Vod> vods = new ArrayList<Vod>();
        if (user != null) {
            vods = vodService.findPage(offset, pageRows, user, syubetu);
            total = vodRepo.countByUserAndSyubetu(user, syubetu);
            prev = (offset + pageRows) < total ? def.getNavibtnEnable()
                    : def.getNavibtnDisable();
        }
        String next = def.getNavibtnDisable();
        // VOD(バイタル｜血液検査）一覧の状態を保持するコマンドをセッションに登録
        VodListCmd vodListCmd =
                new VodListCmd(prev, next, vms, vods, offset, total, syubetu, user);
        rq.setAttribute(AppUtil.MKEY_STS, vodListCmd,
                RequestAttributes.SCOPE_SESSION);
        model.addAttribute(AppUtil.MKEY_STS, vodListCmd);
        return AppUtil.VIEW_VOD_LIST[syubetu];

    }

    /**
     * 前のページ(offset+pageRows)を表示する.
     * <p>
     * 注意：実際のデータの並び順から言えば次のページ。
     * </p>
     * 
     * @param model
     * @param rq
     * @return
     */
    @RequestMapping(value = "/prevPage", method = RequestMethod.GET)
    public String prevPage(Model model, WebRequest rq) {
        // 保存していたVOD一覧状態コマンドを取得
        VodListCmd cmd = (VodListCmd) rq.getAttribute(AppUtil.MKEY_STS,
                RequestAttributes.SCOPE_SESSION);

        // ページ行数をセット
        int pageRows = def.pageRows(cmd.syubetu);
        if (cmd.prev.equals(def.getNavibtnEnable()) && cmd.user != null) {
            cmd.offset = cmd.offset + pageRows;
            List<Vod> vods =
                    vodService.findPage(cmd.offset, pageRows, cmd.user, cmd.syubetu);
            cmd.vods = vods;
            cmd.prev = (cmd.offset + pageRows) < cmd.total ? def.getNavibtnEnable()
                    : def.getNavibtnDisable();
            cmd.next = def.getNavibtnEnable();
        }
        model.addAttribute(AppUtil.MKEY_STS, cmd);

        // 種別に応じたビューを表示する
        return AppUtil.VIEW_VOD_LIST[cmd.syubetu];
    }

    /**
     * 次のページ(offset-pageRows)を表示する。
     * <p>
     * 注意：実際のデータの並び順から言えば前のページ。
     * </p>
     * 
     * @param model
     * @param rq
     * @return
     */
    @RequestMapping(value = "/nextPage", method = RequestMethod.GET)
    public String nextPage(Model model, WebRequest rq) {
        // 保存していたVOD一覧状態コマンドを取得
        VodListCmd cmd = (VodListCmd) rq.getAttribute(AppUtil.MKEY_STS,
                RequestAttributes.SCOPE_SESSION);

        // ページ行数をセット
        int pageRows = def.pageRows(cmd.syubetu);
        if (cmd.next.equals(def.getNavibtnEnable())) {
            cmd.offset = cmd.offset - pageRows;
            List<Vod> vods =
                    vodService.findPage(cmd.offset, pageRows, cmd.user, cmd.syubetu);
            cmd.vods = vods;
            cmd.prev = def.getNavibtnEnable();
            cmd.next =
                    cmd.offset > 0 ? def.getNavibtnEnable() : def.getNavibtnDisable();
        }
        model.addAttribute(AppUtil.MKEY_STS, cmd);

        // 種別に応じたビューを表示する
        return AppUtil.VIEW_VOD_LIST[cmd.syubetu];
    }
}
