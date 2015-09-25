package com.itrane.boothealth.controller;

import static com.itrane.boothealth.AppUtil.getUser;
import static com.itrane.boothealth.AppUtil.getUserName;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itrane.boothealth.AppConst;
import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.cmd.VitalCmd;
import com.itrane.boothealth.cmd.VodFormCmd;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;
import com.itrane.boothealth.repo.VodRepository;
import com.itrane.boothealth.service.EntityNotFoundException;
import com.itrane.boothealth.service.VodService;

/**
 * バイタル測定値/血液検査結果の入力処理を行う。
 */
@Controller
public class VodFormController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // %%% リポジトリ %%%%%
    private VodRepository vodRepo;
    private VodService vodService;
    private VitalMstRepository vmRepo;
    private UserRepository userRepo;
    
    // %%% アプリ定数 %%%%%
    private AppConst def;

    // %%% DI：リポジトリ、アプリ定数 %%%%%
    @Autowired
    public VodFormController(VodRepository vodRepo, VodService vodService,
            VitalMstRepository vmRepo, UserRepository userRepo, AppConst def) {
        this.vodRepo = vodRepo;
        this.vodService = vodService;
        this.vmRepo = vmRepo;
        this.userRepo = userRepo;
        this.def = def;
    }

    // VodFormCmd をリクエスト時に Modelにセット
    @ModelAttribute("vodFormCmd")
    private VodFormCmd getVodFormCmd(HttpServletRequest rq, Integer type) {
        if (type == null) {
            type = def.defaultType(); // デフォルト値を設定
        }
        Vod vod = null;
        List<VitalMst> vms = new ArrayList<VitalMst>();
        // ログインユーザーを取得
        UserInfo user = getUser(userRepo, getUserName(rq));
        List<VitalCmd> vcmds = null;
        if (user != null) {
            // ログインユーザーのバイタルマスターを取得
            vms = vmRepo.findAllByUser(user, type);
            // 当日の Vod を取得
            List<Vod> vods = vodRepo.findBySokuteiBi(user,
                    DateTime.now().toString("yyyy/MM/dd"), type);
            if (vods.size() == 0) {
                // 当日の Vod が無ければ空の Vod を作成
                vod = new Vod(user, DateTime.now().toString("yyyy/MM/dd"),
                        type);
                List<Vital> vitals = new ArrayList<Vital>();
                vcmds = createVcmds(vms, vitals, vod);
                vod.setVitals(vitals);
                vodRepo.save(vod);
            } else {
                vod = vods.get(0);
                vcmds = createVcmds(vms, null, vod);
            }
        }
        VodFormCmd cmd =
                new VodFormCmd(vod, vcmds, vod.getSokuteiBi(), vod.getMemo());
        return cmd;
    }

    private List<VitalCmd> createVcmds(List<VitalMst> vms, List<Vital> vitals,
            Vod vod) {
        List<VitalCmd> vcmds = new ArrayList<VitalCmd>();
        if (vitals != null) {
            //-- 新規VOD: バイタルマスターから VitalCmd リストを作成する
            for (VitalMst vm : vms) {
                vitals.add(new Vital(vm.getName(), "", "0", vod, vm));
                vcmds.add(new VitalCmd(vm.getName(), "", "0", vm.getJikan(),
                        vm.getType(), "", vm.getKijunMin().doubleValue(),
                        vm.getKijunMax().doubleValue()));
            }
        } else {
            //-- 既存VOD:
            if (vod.getVitals().size()==0) {
                //VODは既存のものだが、バイタルリストはまだ初期化されていない
                //バイタルマスターから VitalCmd リストを作成する
                for (VitalMst vm : vms) {
                    vod.getVitals().add(new Vital(vm.getName(), "", "0", vod, vm));
                    vcmds.add(new VitalCmd(vm.getName(), "", "0", vm.getJikan(),
                            vm.getType(), "", vm.getKijunMin().doubleValue(),
                            vm.getKijunMax().doubleValue()));
                }
            } else {
                //バイタルリストは初期化されている。
                //バイタルリストから VitalCmd リストを作成する
                for (Vital vt : vod.getVitals()) {
                    VitalMst vm = vt.getVitalM();
                    vcmds.add(new VitalCmd(vm.getName(), vt.getSokuteiJikan(),
                            vt.getSokuteiTi(), vm.getJikan(), vm.getType(), "",
                            vm.getKijunMin().doubleValue(),
                            vm.getKijunMax().doubleValue()));
                }
            }
        }
        return vcmds;
    }

    /**
     * updateVod GETリクエストに応答. 
     * <p>
     * 実行前に @ModelAttribute(VOD_FORM_CMD)により model にセットされる
     * </p>
     * 
     * @return レスポンスビュー (/views/vod/vodForm | bldForm)
     */
    @RequestMapping(value = {
            "/updateVod"
    }, method = RequestMethod.GET)
    public String editVod(Integer type) {
        if (type == null) {
            type = def.defaultType();
        }
        return AppUtil.VIEW_VOD_FORM[type];
    }

    /**
     * updateVod POSTリクエストに応答、 vod を更新する
     * 
     * @param vod
     *            システム日の Vod
     * @return レスポンスビュー (/views/vod/vodForm | bldForm)
     * @throws EntityNotFoundException
     */
    @RequestMapping(value = "/updateVod", method = RequestMethod.POST)
    public String saveVod(
            @ModelAttribute(value = "vodFormCmd") VodFormCmd vodFormCmd,
            final @Valid VodFormCmd inputCmd, Model model, BindingResult result,
            Integer type) {
        vodFormCmd.checkErrorsAndUpdateVod(inputCmd);
        vodService.updateVod(inputCmd.getVod());
        model.addAttribute("vodFormCmd", inputCmd);
        if (type == null) {
            type = def.defaultType();
        }
        return AppUtil.VIEW_VOD_FORM[type];
    }
}
