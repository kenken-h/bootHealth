package com.itrane.boothealth.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.itrane.boothealth.AppMsg;
import com.itrane.boothealth.AppConst;
import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.repo.KusuriMstRepository;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;
import com.itrane.boothealth.repo.VodRepository;
import com.itrane.boothealth.view.excel.KmExcelBuilder;
import com.itrane.boothealth.view.excel.UserExcelBuilder;
import com.itrane.boothealth.view.excel.VmExcelBuilder;
import com.itrane.boothealth.view.excel.VodExcelBuilder;
import com.itrane.boothealth.view.pdf.KmPdfBuilder;
import com.itrane.boothealth.view.pdf.UserPdfBuilder;
import com.itrane.boothealth.view.pdf.VmPdfBuilder;
import com.itrane.boothealth.view.pdf.VodPdfBuilder;

/**
 * エクスポート処理を行う。
 */
@Controller
public class ExportController {

    //private final Logger log = LoggerFactory.getLogger(getClass());

    // %%% リポジトリ %%%%%
    private VodRepository vodRepo;
    private VitalMstRepository vmRepo;
    private KusuriMstRepository kmRepo;
    private UserRepository userRepo;

    // %%% アプリ定数 %%%%%
    private AppMsg msg;
    private AppConst def;

    // %%% DI：リポジトリ、アプリ定数 %%%%%
    @Autowired
    public ExportController(VodRepository vodRepo, VitalMstRepository vmRepo,
            KusuriMstRepository kmRepo, UserRepository userRepo, AppMsg msg,
            AppConst def) {
        this.vodRepo = vodRepo;
        this.vmRepo = vmRepo;
        this.kmRepo = kmRepo;
        this.msg = msg;
        this.def = def;
        this.userRepo = userRepo;
    }

    // #############################
    // Excell ファイルをエクスポート
    // #############################

    /**
     * ログインユーザーデータを Excel ファイルとしてエクスポートする.
     * 
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportUserExcel", method = RequestMethod.GET)
    public ModelAndView exportUserExcel(HttpServletRequest rq) {
        ModelAndView mav = new ModelAndView(new UserExcelBuilder());
        mav.addObject(AppUtil.MKEY_LST, userRepo.findAll());
        mav.addObject(AppUtil.MKEY_TITLE, msg.USER_LIST_TITLE);
        mav.addObject(AppUtil.MKEY_FILE_NAME, createFileName("user-"));
        return mav;
    }

    /**
     * VOD データを Excel ファイルとしてエクスポートする.
     * 
     * @param type
     *            測定マスターのタイプ(0:バイタル, 1:血液検査)
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportVodExcel", method = RequestMethod.GET)
    public ModelAndView exportVodExcel(@RequestParam("type") Integer type,
            HttpServletRequest rq) {
        String userName = AppUtil.getUserName(rq);
        String prefix = type == def.getSokuteiVital() ? "vital-" : "bloodTest-";
        return createVodMav(new VodExcelBuilder(), type, AppUtil.getUserName(rq),
                createFileName(prefix + userName + "-"));
    }

    /**
     * 薬マスターデータを Excel ファイルとしてエクスポートする.
     * 
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportKmExcel", method = RequestMethod.GET)
    public ModelAndView exportKmExcel(HttpServletRequest rq) {
        ModelAndView mav = new ModelAndView(new KmExcelBuilder());
        mav.addObject(AppUtil.MKEY_FILE_NAME, createFileName("km-"));
        mav.addObject(AppUtil.MKEY_LST, kmRepo.findAllOrderByName());
        mav.addObject(AppUtil.MKEY_TITLE, msg.KM_LIST_TITLE);
        return mav;
    }

    /**
     * バイタルマスターデータを Excel ファイルとしてエクスポートする.
     * 
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportVmExcel", method = RequestMethod.GET)
    public ModelAndView exportVmExcel(@RequestParam("type") Integer type,
            HttpServletRequest rq) {
        String fileName = type==0 ? createFileName("vm-") : createFileName("bm-");
        return createVmMav(new VmExcelBuilder(), type,
                AppUtil.getUserName(rq), fileName);
    }

    /**
     * 血液検査マスターデータを Excel ファイルとしてエクスポートする.
     * 
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportBmExcel", method = RequestMethod.GET)
    public ModelAndView exportBmExcel(HttpServletRequest rq) {
        return createVmMav(new VmExcelBuilder(), def.getSokuteiBlood(),
                AppUtil.getUserName(rq), createFileName("bm-"));
    }

    // #############################
    // PDF をエクスポート
    // #############################

    /**
     * ログインユーザデータを PDFとしてエクスポートする.
     * 
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportUserPdf", method = RequestMethod.GET)
    public ModelAndView exportUserPdf(HttpServletRequest rq) {
        ModelAndView mav = new ModelAndView(new UserPdfBuilder());
        mav.addObject(AppUtil.MKEY_LST, userRepo.findAll());
        mav.addObject(AppUtil.MKEY_TITLE, msg.USER_LIST_TITLE);
        return mav;
    }

    /**
     * VOD データを PDFとしてエクスポートする.
     * 
     * @param type
     *            測定マスターのタイプ(1:バイタル, 2:血液検査)
     * @param type
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportVodPdf", method = RequestMethod.GET)
    public ModelAndView exportVodPdf(@RequestParam("type") Integer type,
            HttpServletRequest rq) {
        return createVodMav(new VodPdfBuilder(), type, AppUtil.getUserName(rq), null);
    }

    // バイタル｜血液検査 エクスポート用の MAV を作成する
    private ModelAndView createVodMav(View view, int syubetu, String userName,
            String fileName) {
        ModelAndView mav = new ModelAndView(view);
        UserInfo user = AppUtil.getUser(userRepo, userName);
        if (user != null) {
            // ユーザーは必ず見つかることを想定
            mav.addObject(AppUtil.MKEY_LST, vmRepo.findAllByUser(user, syubetu));
            mav.addObject(AppUtil.MKEY_VODS, vodRepo.findAllByUser(user, syubetu));
        } else {
            // 想定外のことが発生
            mav.addObject(AppUtil.MKEY_LST, new ArrayList<VitalMst>());
            mav.addObject(AppUtil.MKEY_VODS, new ArrayList<Vod>());
        }
        mav.addObject(AppUtil.MKEY_TITLE, msg.vodListTitle(syubetu));
        mav.addObject(AppUtil.MKEY_HEAD_LABEL, msg.headLabel(syubetu));
        if (fileName != null) {
            mav.addObject(AppUtil.MKEY_FILE_NAME, fileName);
            mav.addObject(AppUtil.MKEY_SHEET_NAME, msg.vmListTitle(syubetu));
        }
        return mav;
    }

    /**
     * 薬マスターデータを PDFとしてエクスポートする.
     * 
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportKmPdf", method = RequestMethod.GET)
    public ModelAndView exportKmPdf(HttpServletRequest rq) {
        ModelAndView mav = new ModelAndView(new KmPdfBuilder());
        mav.addObject(AppUtil.MKEY_LST, kmRepo.findAllOrderByName());
        mav.addObject(AppUtil.MKEY_TITLE, msg.KM_LIST_TITLE);
        return mav;
    }

    /**
     * バイタルマスターデータを PDFとしてエクスポートする.
     * 
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportVmPdf", method = RequestMethod.GET)
    public ModelAndView exportVmPdf(@RequestParam("type") Integer type,
            HttpServletRequest rq) {
        return createVmMav(new VmPdfBuilder(), type,
                AppUtil.getUserName(rq), null);
    }

    /**
     * 血液検査マスターデータを PDFとしてエクスポートする.
     * 
     * @param rq
     * @return
     */
    @RequestMapping(value = "exportBmPdf", method = RequestMethod.GET)
    public ModelAndView exportBmPdf(HttpServletRequest rq) {
        return createVmMav(new VmPdfBuilder(), def.getSokuteiBlood(),
                AppUtil.getUserName(rq), null);
    }

    // バイタルマスター・エクスポート用の MAV を作成
    private ModelAndView createVmMav(View view, int syubetu, String userName,
            String fileName) {
        ModelAndView mav = new ModelAndView(view);
        UserInfo user = AppUtil.getUser(userRepo, userName);
        mav.addObject(AppUtil.MKEY_LST, user != null
                ? vmRepo.findAllByUser(user, syubetu) : new ArrayList<VitalMst>());
        mav.addObject(AppUtil.MKEY_TITLE, msg.vmListTitle(syubetu));
        mav.addObject(AppUtil.MKEY_HEAD_LABEL, msg.headLabel(syubetu));
        if (fileName != null) {
            mav.addObject(AppUtil.MKEY_FILE_NAME, fileName);
        }
        return mav;
    }

    // #############################
    // Helper
    // #############################
    // prefix とシステム日時から出力するファイル名を組み立てる
    private String createFileName(String prefix) {
        return new StringBuilder(prefix)
                .append(DateTime.now().toString("yyyyMMdd-hhmmss")).append(".xls")
                .toString();
    }
}
