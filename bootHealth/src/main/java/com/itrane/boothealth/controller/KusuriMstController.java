package com.itrane.boothealth.controller;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.itrane.boothealth.AppMsg;
import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.cmd.StatusCmd;
import com.itrane.boothealth.model.KusuriMst;
import com.itrane.boothealth.repo.KusuriMstRepository;
import com.itrane.boothealth.service.KusuriMstService;
import com.itrane.common.util.model.DataTableObject;
import com.itrane.common.util.model.DtAjaxData;

/**
 * 薬マスターのCRUD処理を行う。
 */
@Controller
public class KusuriMstController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private KusuriMstRepository repo;
    private KusuriMstService service;

    private AppMsg msg;

    @Autowired
    public KusuriMstController(KusuriMstRepository repo, KusuriMstService service,
            AppMsg msg) {
        this.repo = repo;
        this.service = service;
        this.msg = msg;
    }

    /**
     * 薬マスター一覧ビューの表示.
     * 
     * @return ModelAndView
     */
    @RequestMapping(value = "/admin/kusuriMstList", method = RequestMethod.GET)
    public ModelAndView kusuriMstList() {
        ModelAndView mav = new ModelAndView(AppUtil.VIEW_KM_LIST);
        KusuriMst km = new KusuriMst();
        mav.addObject(AppUtil.MKEY_FRM, km);
        return mav;
    }

    /**
     * 取得ページ情報に基づいて DataTable を更新する.
     * 
     * @param rq
     * @return 新しいページ情報
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/admin/kusuriMstPage", produces = "text/html;charset=UTF-8")
    public @ResponseBody String getKMPage(HttpServletRequest rq)
            throws UnsupportedEncodingException {
        List<KusuriMst> kms = null;
        // Query文字列からdataTables ページ情報を取得
        DtAjaxData dta = AppUtil.fromJson(DtAjaxData.class, rq.getQueryString());
        // 薬マスターの全登録件数を取得
        long total = repo.count();
        // ページ情報から１ページ分の薬マスターデータを取得
        kms = service.findByPage(dta.start, dta.length, dta.search.value,
                dta.getSortDir(), dta.getSortCol());
        // テーブルページ情報を作成して返す
        DataTableObject<KusuriMst> dt =
                new DataTableObject<KusuriMst>(kms, dta.draw, total, (int) total);
        return AppUtil.toJson(dt);
    }

    /**
     * 編集のために、id で指定した薬データをDBから読み込む.
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/admin/editKM/{id}", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    public @ResponseBody String editKM(@PathVariable Long id) {
        KusuriMst km = repo.findOne(id);
        return AppUtil.toJson(km);
    }

    @RequestMapping(value = "/admin/deleteKM/{id}", method = RequestMethod.GET)
    public @ResponseBody String deleteKM(@PathVariable Long id) {
        repo.delete(id);
        return AppUtil.toJson("削除");
    }

    /**
     * 薬マスター保存：ajax 版.
     * 
     * @return
     */
    @RequestMapping(value = "/admin/saveKM")
    public @ResponseBody StatusCmd saveKM(@RequestBody KusuriMst km) {
        Set<ConstraintViolation<KusuriMst>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(km);
        StatusCmd cmd = new StatusCmd(1, 0, "");
        ModelAndView mav = new ModelAndView();
        if (violations.size() > 0) {
            Iterator<ConstraintViolation<KusuriMst>> itr = violations.iterator();
            while (itr.hasNext()) {
                ConstraintViolation<KusuriMst> cs = itr.next();
                cmd.getFldErrors().put(cs.getPropertyPath().toString(),
                        cs.getMessage());
            }
        } else {
            try {
                service.save(km);
                cmd.setMessage("薬：" + km.getName()
                        + (km.getId() == null ? msg.MSG_CREATE : msg.MSG_UPDATE));
            } catch (Exception ex) {
                cmd.setStatus(9);
                String errFld = AppUtil.validDuplicateError(ex.getLocalizedMessage());
                if (errFld != null) {
                    cmd.getFldErrors().put(errFld, msg.VALID_NAME_DUPLICATE);
                }
            }
        }
        return cmd;
    }
}
