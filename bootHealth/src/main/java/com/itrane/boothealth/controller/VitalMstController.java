package com.itrane.boothealth.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.itrane.boothealth.AppMsg;
import com.itrane.boothealth.AppConst;
import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.cmd.StatusCmd;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;
import com.itrane.boothealth.service.VitalMstService;
import com.itrane.common.util.model.DataTableObject;
import com.itrane.common.util.model.DtAjaxData;

/**
 * バイタルマスターのCRUD処理を行う。
 */
@Controller
public class VitalMstController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String ENTITY_NAME = "指定されたマスターデータ";

    private VitalMstRepository repo;
    private VitalMstService service;
    private UserRepository userRepo;
    
    private AppMsg msg;
    private AppConst def;

    @Autowired
    public VitalMstController(VitalMstRepository repo, VitalMstService service,
            UserRepository userRepo, AppMsg msg, AppConst def) {
        this.repo = repo;
        this.service = service;
        this.userRepo = userRepo;
        this.msg = msg;
        this.def = def;
    }

    // ##############################
    // バイタルマスター/血液検査マスター
    // ##############################
    /**
     * <p>
     * LIST: バイタルまたは血液検査マスターの一覧ビューを表示する.
     * </p>
     * 
     * @param type
     *            マスターの種別（SOKUTEI_VITAL | SOKUTEI_BLOOD)
     * @return
     */
    @RequestMapping(value = "/vitalMstList", method = RequestMethod.GET)
    public ModelAndView vitalMstList(Integer type) {
        ModelAndView mav = new ModelAndView("views/vitalMstList");
        if (type == null) {
            // 測定タイプが指定されない場合はデフォルト値を設定
            type = def.defaultType();
        }
        mav.addObject(AppUtil.MKEY_FRM, new VitalMst());
        mav.addObject("vmTypes", def.vmTypes(type));
        mav.addObject("headLabel", msg.headLabel(type));
        mav.addObject(AppUtil.MKEY_STS,
                new StatusCmd(type, msg.vmListTitle(type), def.pageUrl(type),
                        def.importUrl(type)));

        return mav;
    }

    /**
     * <p>
     * PAGE: バイタルマスター・ビューの dataTables で指定ページのデータを取得する.
     * </p>
     * 
     * @param HttpServletRequest
     *            : dataTables の表示ページ、表示件数、ソート列等を取得。
     * @return String: DataTableObject<VitalMst> の JSon形式
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/vmPage", produces = "text/html;charset=UTF-8")
    public @ResponseBody String getVmPage(HttpServletRequest rq)
            throws UnsupportedEncodingException {
        return getPage(rq, def.getSokuteiVital());
    }

    /**
     * <p>
     * PAGE: 血液検査マスター・ビューの dataTables で指定ページのデータを取得する.
     * </p>
     * 
     * @param HttpServletRequest
     *            : dataTables の表示ページ、表示件数、ソート列等を取得。
     * @return String: DataTableObject<VitalMst> の JSon形式
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/bmPage", produces = "text/html;charset=UTF-8")
    public @ResponseBody String getBmPage(HttpServletRequest rq)
            throws UnsupportedEncodingException {
        return getPage(rq, def.getSokuteiBlood());
    }

    private String getPage(HttpServletRequest rq, int syubetu)
            throws UnsupportedEncodingException {
        UserInfo user = AppUtil.getUser(userRepo, AppUtil.getUserName(rq));
        List<VitalMst> vms = new ArrayList<VitalMst>();
        DtAjaxData dta = AppUtil.fromJson(DtAjaxData.class, rq.getQueryString());
        long total = 0L;
        if (user != null) {
            total = repo.countByUser(user, syubetu);
            vms = service.findByPage(user, syubetu, dta.start, dta.length,
                    dta.search.value, dta.getSortDir(), dta.getSortCol());
        }
        DataTableObject<VitalMst> dt =
                new DataTableObject<VitalMst>(vms, dta.draw, total, (int) total);
        return AppUtil.toJson(dt);
    }

    /**
     * <p>
     * EDIT: id で指定したバイタルマスター/血液検査マスターを編集.
     * </p>
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/editVM/{id}", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    public @ResponseBody String editVM(@PathVariable Long id) {
        VitalMst vm = repo.findOne(id);
        return AppUtil.toJson(vm);
        // return vm;
    }

    /**
     * SAVE: 編集したバイタルマスターを保存（新規｜更新）
     * 
     * @param vm
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/saveVM")
    public @ResponseBody StatusCmd saveVM(@RequestBody VitalMst vm,
            HttpServletRequest rq) {
        // バイタルマスターの保存にはユーザーと種別が必要
        UserInfo user = AppUtil.getUser(userRepo, AppUtil.getUserName(rq));
        int type = def.getSokuteiVital();
        return save(vm, type, user);
    }

    @RequestMapping(value = "/saveBM")
    public @ResponseBody StatusCmd saveBM(@RequestBody VitalMst bm,
            HttpServletRequest rq) {
        // バイタルマスターの保存にはユーザーと種別が必要
        UserInfo user = AppUtil.getUser(userRepo, AppUtil.getUserName(rq));
        int type = def.getSokuteiBlood();
        return save(bm, type, user);
    }

    private StatusCmd save(VitalMst vm, int type, UserInfo user) {
        Set<ConstraintViolation<VitalMst>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(vm);
        StatusCmd cmd = new StatusCmd(1, 0, "");
        if (violations.size() > 0) {
            Iterator<ConstraintViolation<VitalMst>> itr = violations.iterator();
            while (itr.hasNext()) {
                ConstraintViolation<VitalMst> cs = itr.next();
                cmd.getFldErrors().put(cs.getPropertyPath().toString(),
                        cs.getMessage());
            }
        } else {
            try {
                vm.setSyubetu(type);
                vm.setUser(user);
                service.save(vm);
                cmd.setMessage(vm.getName() + (vm.getId() == null ? msg.MSG_CREATE
                        : msg.MSG_UPDATE));
            } catch (Exception ex) {
                String errFld = AppUtil.validDuplicateError(ex.getLocalizedMessage());
                if (errFld != null) {
                    cmd.getFldErrors().put(errFld, msg.VALID_NAME_DUPLICATE);
                }
            }
        }
        return cmd;
    }

    /**
     * <p>
     * DELETE: id で指定したマスターを削除する.
     * </p>
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteVM/{id}", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    public @ResponseBody String deleteVM(@PathVariable Long id) {
        repo.delete(id);
        return AppUtil.toJson(ENTITY_NAME + msg.MSG_DELETE);
    }

    @RequestMapping(value = "/deleteAllVM", method = RequestMethod.GET)
    public String deleteAllVM() {
        return null;
    }
}
