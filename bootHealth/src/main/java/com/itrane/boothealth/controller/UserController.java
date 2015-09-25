package com.itrane.boothealth.controller;

import static com.itrane.boothealth.AppUtil.getUser;
import static com.itrane.boothealth.AppUtil.getUserName;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.itrane.boothealth.cmd.StatusCmd;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.service.EntityNotFoundException;
import com.itrane.boothealth.service.UserService;
import com.itrane.common.util.model.DataTableObject;
import com.itrane.common.util.model.DtAjaxData;

/**
 * ユーザー情報の処理を行うコントローラ.
 */
@Controller
@Scope("session")
public class UserController {

    final private Logger log = LoggerFactory.getLogger(getClass());

    private static final String ENTITY_NAME = "指定されたユーザー";

    private UserRepository userRepo;
    private UserService userService;
    
    private AppMsg msg;
    private AppConst def;
    
    private String tabName = "#kojin";

    @Autowired
    public UserController(UserRepository userRepo, UserService userService,
            AppMsg msg, AppConst def) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.msg = msg;
        this.def = def;
    }

    // ---------------- admin -----------------------------

    // ###########################
    // ユーザー一覧
    // ###########################
    /**
     * LIST:ユーザー一覧ビューを表示する. (管理者権限)
     * 
     * @param flg
     * @return
     */
    @RequestMapping(value = {
            "/admin/userList"
    }, method = RequestMethod.GET)
    public ModelAndView userList(String flg) {
        ModelAndView mav = new ModelAndView(AppUtil.VIEW_USER_LIST);
        mav.addObject(AppUtil.MKEY_STS, new StatusCmd()); // ステータス：初期状態
        mav.addObject(AppUtil.MKEY_FRM, new UserInfo());
        return mav;
    }

    /**
     * 取得ページ情報に基づいて DataTable を更新する.
     * 
     * @param rq
     * @return DataTableObject の JSON文字列
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/admin/userPage", produces = "text/html;charset=UTF-8")
    public @ResponseBody String getUserPage(HttpServletRequest rq)
            throws UnsupportedEncodingException {
        String qs = rq.getQueryString();
        List<UserInfo> users = null;
        DtAjaxData dta =
                AppUtil.fromJson(DtAjaxData.class, rq.getQueryString());

        long total = userRepo.count();
        users = userService.findPage(dta.start, dta.length, dta.search.value,
                dta.getSortDir(), dta.getSortCol());
        DataTableObject<UserInfo> dt = new DataTableObject<UserInfo>(users,
                dta.draw, total, (int) total);
        return AppUtil.toJson(dt);
    }

    /**
     * 指定された ユーザーID を取得する
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/admin/editUser/{id}", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    public @ResponseBody String editUser(@PathVariable Long id) {
        UserInfo user = userRepo.findOne(id);
        return AppUtil.toJson(user);
    }

    /**
     * DELETE: id で指定したマスターを削除する.
     * 
     * @param id
     * @return
     * @throws EntityNotFoundException
     */
    @RequestMapping(value = "/admin/deleteUser/{id}", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    public @ResponseBody String deleteUser(@PathVariable Long id)
            throws EntityNotFoundException {
        userService.deleteUser(id);
        return AppUtil.toJson(ENTITY_NAME + msg.MSG_DELETE);
    }

    /**
     * SAVE:ユーザー保存（ajax版).
     * 
     * @param cmd
     * @return
     */
    @RequestMapping(value = "/admin/saveUser")
    public @ResponseBody StatusCmd saveUser(@RequestBody UserInfo user) {
        Set<ConstraintViolation<UserInfo>> violations = Validation
                .buildDefaultValidatorFactory().getValidator().validate(user);
        StatusCmd cmd = new StatusCmd(1, 0, "");
        if (violations.size() > 0) {
            cmd.setStatus(9);
            Iterator<ConstraintViolation<UserInfo>> itr = violations.iterator();
            while (itr.hasNext()) {
                ConstraintViolation<UserInfo> cs = itr.next();
                cmd.getFldErrors().put(cs.getPropertyPath().toString(),
                        cs.getMessage());
            }
        } else {
            try {
                userService.save(user);
                cmd.setMessage("ユーザー：" + user.getName() + (user.getId() == null
                        ? msg.MSG_CREATE : msg.MSG_UPDATE));
            } catch (Exception ex) {
                cmd.setStatus(9);
                String errFld =
                        AppUtil.validDuplicateError(ex.getLocalizedMessage());
                if (errFld != null) {
                    cmd.getFldErrors().put(errFld,
                            msg.VALID_NAME_DUPLICATE);
                }
            }
        }
        return cmd;
    }

    // ###########################
    // ユーザーフォーム
    // ###########################
    /**
     * 個人カルテ入力フォームの表示.
     * 
     * @return ビュー
     */
    @RequestMapping(value = "/user/userForm", method = RequestMethod.GET)
    public ModelAndView userForm(HttpServletRequest rq) {
        ModelAndView mav = new ModelAndView(AppUtil.VIEW_USER_FORM);
        // 現在のログインユーザー名でユーザーを取得（登録済みユーザーしかログインできないので必ず見つかる）
        UserInfo user = getUser(userRepo, getUserName(rq));
        mav.addObject("gengers", def.getGenders());
        mav.addObject("rhTypes", def.getRhTypes());
        mav.addObject("bloodTypes", def.getBloodTypes());
        mav.addObject("pastIllnesses", def.getPastIllnesses());
        // StatusCmd cmd = new StatusCmd(0, 0, "");
        StatusCmd cmd = new StatusCmd();
        cmd.setPageTitle(tabName);
        return getModelAndView(mav, cmd, user);
    }

    /**
     * 個人カルテ入力フォームのデータ・submit処理.
     * 
     * @return
     */
    @RequestMapping(value = "/user/userForm", method = RequestMethod.POST)
    public ModelAndView userFormSubmit(
            @Valid @ModelAttribute("user") UserInfo user, BindingResult result,
            @ModelAttribute("cmd") StatusCmd cmd) {
        ModelAndView mav = new ModelAndView(AppUtil.VIEW_USER_FORM);
        if (!result.hasErrors()) {
            try {
                userService.save(user);
                cmd.setMessage("ユーザー：" + user.getName() + (user.getId() == null
                        ? msg.MSG_CREATE : msg.MSG_UPDATE));
            } catch (Exception ex) {
                cmd.setStatus(9);
                String errFld =
                        AppUtil.validDuplicateError(ex.getLocalizedMessage());
                if (errFld != null) {
                    cmd.getFldErrors().put(errFld,
                            msg.VALID_NAME_DUPLICATE);
                }
            }
        } else {
            cmd.setStatus(9);
        }
        mav.addObject("gengers", def.getGenders());
        mav.addObject("rhTypes", def.getRhTypes());
        mav.addObject("bloodTypes", def.getBloodTypes());
        mav.addObject("pastIllnesses", def.getPastIllnesses());
        cmd.setPageTitle(tabName);
        return getModelAndView(mav, cmd, user);
    }

    /**
     * グーグル乗り換え案内.
     * 
     * @param transitCmd
     * @return transitUrl
     */
    @RequestMapping(value = "/gTransit")
    public @ResponseBody StatusCmd gTransit(@RequestBody UserInfo user) {
        StatusCmd cmd = new StatusCmd(1, 0, "ルート成功");
        String transitUrl = "";
        String from = user.getByoin1Jusyo();
        String to = user.getByoin2Jusyo();
        if (from.length() > 0 && to.length() > 0) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdfD = new SimpleDateFormat(def.getDateFormat());
            SimpleDateFormat sdfT = new SimpleDateFormat(def.getTimeFormat());
            String date = sdfD.format(cal.getTime());
            String time = sdfT.format(cal.getTime());

            transitUrl =
                    AppUtil.getTransitURL(from, to, date, time, "arr", false);
            cmd.setMessage(transitUrl);
        }
        return cmd;
    }
    
    @RequestMapping(value = "/user/setTabName")
    public @ResponseBody StatusCmd setTabName(@RequestBody StatusCmd cmd) {
        this.tabName = cmd.getPageTitle();
        return cmd;
    }


    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // helper
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%
    /*
     * 渡された ModelAndView に StatusCmd と UserInfo をセットして返す。
     */
    private ModelAndView getModelAndView(ModelAndView mav, StatusCmd cmd,
            UserInfo user) {
        mav.addObject(AppUtil.MKEY_STS, cmd);
        mav.addObject(AppUtil.MKEY_FRM, user);
        return mav;
    }
}
