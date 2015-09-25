package com.itrane.boothealth.controller;

import static com.itrane.boothealth.AppUtil.IMPORT_BM;
import static com.itrane.boothealth.AppUtil.IMPORT_KM;
import static com.itrane.boothealth.AppUtil.IMPORT_USER;
import static com.itrane.boothealth.AppUtil.IMPORT_VM;
import static com.itrane.boothealth.AppUtil.IMPORT_VOD_BLD;
import static com.itrane.boothealth.AppUtil.IMPORT_VOD_VTL;
import static com.itrane.boothealth.AppUtil.getUser;
import static com.itrane.boothealth.AppUtil.getUserName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.itrane.boothealth.AppMsg;
import com.itrane.boothealth.AppConst;
import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.KusuriMst;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.repo.KusuriMstRepository;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;
import com.itrane.boothealth.repo.VodRepository;
import com.itrane.boothealth.service.VodService;

/**
 * インポート処理のためのコントローラ.
 * importXX() --> upload() --> uploadXXData()
 * <ul>
 * <li>importVital: バイタルのインポート --> upload(IMPORT_VOD_VTL)
 * <li>importBlood: 血液検査のインポート --> upload(IMPORT_VOD_BLD)
 * <li>importUser: ユーザーマスター --> upload(IMPORT_USER)
 * <li>importKM: 薬マスター --> upload(IMPORT_KM)
 * <li>importVM: バイタルマスター --> upload(IMPORT_VM)
 * <li>importBM: 血液検査マスター --> upload(IMPORT_BM)
 * </ul>
 */
@Controller
public class ImportController {

    //final private Logger log = LoggerFactory.getLogger(getClass());

    private static final int USER_COL_NUM = 5;
    private static final String[] USER_HEADERS =
            { "ユーザー名", "パスワード", "氏名", "よみ", "ロール" };

    private static final int KM_COL_NUM = 4;
    private static final String[] KM_HEADERS = { "薬品名", "説明", "非ジェネリック", "服用量" };

    private static final int VM_COL_NUM = 7;
    private static final String[] VM_HEADERS =
            { "順序", "名称", "タイプ", "", "基準値最小", "基準値最大", "種別" };

    private VodRepository vodRepo;
    private VodService vodService;
    private VitalMstRepository vmRepo;
    private UserRepository userRepo;
    private KusuriMstRepository kmRepo;

    private AppMsg msg;
    private AppConst def;

    @Autowired
    public ImportController(VodService vodService, VodRepository vodRepo,
            VitalMstRepository vmRepo, KusuriMstRepository kmRepo,
            UserRepository userRepo, AppMsg msg, AppConst def) {
        this.vodService = vodService;
        this.vodRepo = vodRepo;
        this.vmRepo = vmRepo;
        this.kmRepo = kmRepo;
        this.msg = msg;
        this.def = def;
        this.userRepo = userRepo;
    }

    // ##################
    // Vod インポート
    // ##################
    /**
     * バイタル測定値データのインポート
     * 
     * @param rq
     * @param res
     * @param syubetu
     * @return
     */
    @RequestMapping(value = "/importVital", method = RequestMethod.POST)
    public @ResponseBody String importVital(MultipartHttpServletRequest rq,
            HttpServletResponse res, Integer syubetu) {
        return upload(rq, res, IMPORT_VOD_VTL);
    }

    /**
     * 血液検査結果データのインポート
     * 
     * @param rq
     * @param res
     * @param syubetu
     * @return
     */
    @RequestMapping(value = "/importBlood", method = RequestMethod.POST)
    public @ResponseBody String importBlood(MultipartHttpServletRequest rq,
            HttpServletResponse res, Integer syubetu) {
        return upload(rq, res, IMPORT_VOD_BLD);
    }

    /*
     * インポートファイルが正しい列を持っているか VitalMst と付き合わせる.
     */
    private String uploadVodData(String userName, ByteArrayInputStream bis,
            Workbook workbook, int syubetu) {
        if (! workbook.getSheetName(0).equals(msg.vodListTitle(syubetu))) {
            return msg.IMPORT_INVALID_SHEETNAME + workbook.getSheetName(0);
        }
        Sheet sheet = workbook.getSheetAt(0);
        UserInfo user = getUser(userRepo, userName);
        List<VitalMst> vms = new ArrayList<VitalMst>();
        String rtnMsg = null;
        if (user != null) {
            vms = vmRepo.findAllByUser(user, syubetu);
            if (vms.size() == 0) {
                rtnMsg = uploadVMData(userName, bis, workbook, syubetu, 1, vms);
            }
        }
        int startRow = sheet.getFirstRowNum();
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                StringBuilder sb = new StringBuilder("行:" + row.getRowNum() + " : ");
                Iterator<Cell> cellIterator = row.cellIterator();
                if (i == startRow) {
                    // ヘッダのチェック
                    if (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (!cell.getStringCellValue().equals("日付")) {
                            return msg.IMPORT_INVALID_COLNAME
                                    + cell.getStringCellValue();
                        }
                        sb.append(cell.getStringCellValue() + ", ");
                    }
                    int col = 0;
                    while (cellIterator.hasNext() && col < vms.size()) {
                        Cell cell = cellIterator.next();
                        if (!getStringValue(cell).equals(vms.get(col).getName())) {
                            return msg.IMPORT_INVALID_COLNAME
                                    + getStringValue(cell);
                        }
                        sb.append(getStringValue(cell) + ", ");
                        col++;
                    }
                } else {
                    // バイタル/血液検査データをインポートする
                    rtnMsg = importVod(cellIterator, user, syubetu, vms);
                }
            }
        }
        return rtnMsg;
    }

    private String importVod(Iterator<Cell> cellIterator, UserInfo user, int syubetu,
            List<VitalMst> vms) {
        String rtnMsg = null;
        String sokuteiBi = "";
        if (cellIterator.hasNext()) {
            sokuteiBi = getStringValue(cellIterator.next());
        }
        // 読み込んだ日付に等しい Vod を検索
        List<Vod> vods = vodRepo.findBySokuteiBi(user, sokuteiBi, syubetu);
        if (vods.size() == 1) {
            // vod を更新
            Vod vod = vods.get(0);
            List<Vital> vitals = vod.getVitals();
            int i = 0;
            while (cellIterator.hasNext() && i < vms.size()) {
                Vital vt = vitals.get(i);
                String setSokuteiTi = getStringValue(cellIterator.next());
                vt.setSokuteiTi(setSokuteiTi);
                if (vt.getSokuteiJikan() == null
                        || vt.getSokuteiJikan().length() == 0) {
                    vt.setSokuteiJikan(vms.get(i).getJikan());
                }
                i++;
            }
            vodService.updateVod(vod);
        } else {
            // vod を新規作成
            Vod vod = new Vod(user, sokuteiBi, syubetu);
            List<Vital> vitals = new ArrayList<Vital>();
            int i = 0;
            while (cellIterator.hasNext() && i < vms.size()) {
                String name = vms.get(i).getName();
                String jikan = vms.get(i).getJikan();
                String sokuteiTi = getStringValue(cellIterator.next());
                Vital vt = new Vital(name, jikan, sokuteiTi, vod, vms.get(i));
                vitals.add(vt);
                i++;
            }
            vod.setVitals(vitals);
            try {
                if (!vod.getSokuteiBi().equals("0")) {
                    Vod saveVod = vodRepo.save(vod);
                }
            } catch (Exception ex) {
                rtnMsg = "error:" + ex.getMessage();
            }
        }
        return rtnMsg;
    }

    // ####################
    // マスターのインポート
    // ####################
    /**
     * ユーザーのインポート
     * 
     * @param rq
     * @param res
     * @return
     */
    @RequestMapping(value = "/importUser", method = RequestMethod.POST)
    public @ResponseBody String importUser(MultipartHttpServletRequest rq,
            HttpServletResponse res) {
        return upload(rq, res, IMPORT_USER);
    }

    /*
     * ユーザーデータをアップロード
     * ヘッダ列：ユーザー名、パスワード、氏名、よみ、ロール
     * データ列：name, ipass, simei, yomi, roles
     */
    private void uploadUserData(ByteArrayInputStream bis, Workbook workbook) {

        Sheet sheet = workbook.getSheetAt(0);
        int startRow = sheet.getFirstRowNum();
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Iterator<Cell> cellIterator = row.cellIterator();
                // ヘッダのチェック
                if (i == startRow) {
                    for (int col = 0; col < USER_COL_NUM
                            && cellIterator.hasNext(); col++) {
                        Cell cell = cellIterator.next();
                        if (!USER_HEADERS[col].equals(getStringValue(cell))) {
                            throw new IllegalArgumentException(
                                    msg.IMPORT_INVALID_COLNAME
                                            + getStringValue(cell));
                        }
                    }
                } else {
                    List<String> flds = new ArrayList<>();
                    int lastCol = 0;
                    for (int col = 0; col < USER_COL_NUM
                            && cellIterator.hasNext(); col++) {
                        Cell cell = cellIterator.next();
                        flds.add(getStringValue(cell));
                        lastCol = col;
                    }
                    for (int col = lastCol; col < USER_COL_NUM; col++) {
                        flds.add("");
                    }
                    // インポートしたエクセル行のユーザー名が未登録ならユーザーマスターに登録
                    if (userRepo.findByName(flds.get(0)).size() == 0) {
                        userRepo.save(new UserInfo(flds.get(0), flds.get(1),
                                flds.get(2), flds.get(3), flds.get(4)));
                    }
                }
            }
        }
    }

    /**
     * 薬マスターのインポート
     * 
     * @param rq
     * @param res
     * @return
     */
    @RequestMapping(value = "/admin/importKM", method = RequestMethod.POST)
    public @ResponseBody String importKM(MultipartHttpServletRequest rq,
            HttpServletResponse res) {
        return upload(rq, res, IMPORT_KM);
    }

    /*
     * 薬マスターデータをアップロード
     * ヘッダ： "薬品名", "説明", "非ジェネリック", "服用量"
     */
    private void uploadKMData(ByteArrayInputStream bis, Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        int startRow = sheet.getFirstRowNum();
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Iterator<Cell> cellIterator = row.cellIterator();
                // ヘッダのチェック
                if (i == startRow) {
                    for (int col = 0; col < KM_COL_NUM
                            && cellIterator.hasNext(); col++) {
                        Cell cell = cellIterator.next();
                        if (!KM_HEADERS[col].equals(getStringValue(cell))) {
                            throw new IllegalArgumentException(
                                    msg.IMPORT_INVALID_COLNAME
                                            + getStringValue(cell));
                        }
                    }
                } else {
                    List<String> flds = new ArrayList<>();
                    int lastCol = 0;
                    for (int col = 0; col < KM_COL_NUM
                            && cellIterator.hasNext(); col++) {
                        Cell cell = cellIterator.next();
                        flds.add(getStringValue(cell));
                        lastCol = col;
                    }
                    for (int col = lastCol; col < KM_COL_NUM; col++) {
                        flds.add("");
                    }
                    // エクセル行の薬品名が未登録なら、薬マスータに保存
                    if (kmRepo.findByName(flds.get(0)).size() == 0) {
                        kmRepo.save(new KusuriMst(flds.get(0), flds.get(1),
                                flds.get(2), flds.get(3)));
                    }
                }
            }
        }
    }

    /**
     * バイタルマスターのインポート
     * 
     * @param rq
     * @param res
     * @return
     */
    @RequestMapping(value = "/importVM", method = RequestMethod.POST)
    public @ResponseBody String importVM(MultipartHttpServletRequest rq,
            HttpServletResponse res) {
        return upload(rq, res, IMPORT_VM);
    }

    /**
     * 血液検査マスターのインポート
     * 
     * @param rq
     * @param res
     * @return
     */
    @RequestMapping(value = "/importBM", method = RequestMethod.POST)
    public @ResponseBody String importBM(MultipartHttpServletRequest rq,
            HttpServletResponse res) {
        return upload(rq, res, IMPORT_BM);
    }

    /*
     * 指定されたエクセルワークブックの指定シートから
     * バイタル/血液検査マスターデータをアップロードする。
     * マスター単体の場合は sheetIdx は 0 (シート番号は 0 オリジン)
     * バイタル/血液検査データ・インポートのサブ処理の場合 sheetIdx は 1
     */
    private String uploadVMData(String userName, ByteArrayInputStream bis,
            Workbook workbook, int syubetu, int sheetIdx, List<VitalMst> vms) {
        UserInfo user = getUser(userRepo, userName);
        Sheet sheet = workbook.getSheetAt(sheetIdx);
        int startRow = sheet.getFirstRowNum();
        // バイタルマスターと血液検査マスターでは列名が異なる（測定時間｜検査の説明）
        VM_HEADERS[3] = msg.headLabel(syubetu);
        // 指定シートから行を取得する（行数、列数は正しいことを想定。チェックはしていない）
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                // 行ごとの処理を行う
                Iterator<Cell> cellIterator = row.cellIterator();
                if (i == startRow) {
                    // ヘッダ行のチェック
                    for (int col = 0; col < VM_COL_NUM
                            && cellIterator.hasNext(); col++) {
                        Cell cell = cellIterator.next();
                        if (!VM_HEADERS[col].equals(getStringValue(cell))) {
                            return msg.IMPORT_INVALID_COLNAME
                                    + getStringValue(cell);
                        }
                    }
                } else {
                    // データのインポート
                    List<String> flds = new ArrayList<>();
                    int lastCol = 0;
                    for (int col = 0; col < VM_COL_NUM
                            && cellIterator.hasNext(); col++) {
                        Cell cell = cellIterator.next();
                        flds.add(getStringValue(cell));
                        lastCol = col;
                    }
                    for (int col = lastCol; col < VM_COL_NUM; col++) {
                        flds.add("");
                    }
                    // エクセル行のVM名が未登録なら、バイタルマスターに保存
                    // save(1name,3jikan,4kijunMin,5kijunMax,2type,0junnjo,
                    // syubetu, user)
                    // ヘッダ："順序", "名称", "タイプ", "測定時間", "基準値最小", "基準値最大", "種別"
                    if (vmRepo.findByUserAndName(user, syubetu, flds.get(1))
                            .size() == 0) {
                        int junjo = (int) Double.parseDouble(flds.get(0));
                        VitalMst vm = new VitalMst(flds.get(1), flds.get(3),
                                Double.parseDouble(flds.get(4)),
                                Double.parseDouble(flds.get(5)), flds.get(2), junjo,
                                syubetu, user);
                        vm = vmRepo.save(vm);
                        vms.add(vm);
                    }
                }
            }
        }
        return null;
    }

    // =================
    // helper
    // =================
    /*
     * Excelファイルのインポートの前処理としてファイル拡張子、シート数を調べる。
     * 実際の処理はインポートタイプ(impType)に応じて、下請けの UploadXxxx() に任せる。
     */
    private String upload(MultipartHttpServletRequest rq, HttpServletResponse res,
            int impType) {
        Iterator<String> itrator = rq.getFileNames();
        MultipartFile mlf = rq.getFile(itrator.next());
        String fileName = mlf.getOriginalFilename();
        // ファイルタイプとファイル名
        String rtnMsg = null;
        String userName = getUserName(rq);
        if (!mlf.isEmpty()) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(mlf.getBytes());
                // ファイル拡張子を調べてワークブックを作成する
                Workbook workbook = createWorkbook(fileName, bis);
                if (workbook == null) {
                    // ファイルが不正
                    rtnMsg = msg.IMPORT_NOT_EXCEL;
                } else if (!checkSheetNum(impType, workbook.getNumberOfSheets())) {
                    // シート数が不正
                    rtnMsg = msg.IMPORT_INVALID_SHEETNUM;
                } else {
                    switch (impType) {
                    case IMPORT_VOD_VTL:
                        rtnMsg = uploadVodData(userName, bis, workbook, def.getSokuteiVital());
                        break;
                    case IMPORT_VOD_BLD:
                        rtnMsg = uploadVodData(userName, bis, workbook, def.getSokuteiBlood());
                        break;
                    case IMPORT_USER:
                        uploadUserData(bis, workbook);
                        break;
                    case IMPORT_KM:
                        uploadKMData(bis, workbook);
                        break;
                    case IMPORT_VM:
                        uploadVMData(userName, bis, workbook, def.getSokuteiVital(), 0,
                                new ArrayList<VitalMst>());
                        break;
                    case IMPORT_BM:
                        uploadVMData(userName, bis, workbook, def.getSokuteiBlood(), 0,
                                new ArrayList<VitalMst>());
                        break;
                    }
                    if (rtnMsg == null) {
                        rtnMsg = msg.IMPORT_DONE + fileName;
                    }
                }
            } catch (Exception e) {
                // インポート失敗
                rtnMsg = msg.IMPORT_FAIL + fileName + " => " + e.getMessage();
            }
        } else {
            // ファイル名が空
            rtnMsg = msg.IMPORT_FILE_EMPTY + fileName;
        }
        // 処理成功｜失敗のメッセージを返す
        return AppUtil.toJson(rtnMsg);
    }

    // ファイル拡張子を調べて、対応するワークブックを作成する
    private Workbook createWorkbook(String fileName, ByteArrayInputStream bis)
            throws IOException {
        if (fileName.endsWith("xls")) {
            return new HSSFWorkbook(bis);
        } else if (fileName.endsWith("xlsx")) {
            return new XSSFWorkbook(bis);
        } else {
            return null;
        }
    }

    // インポートタイプに応じてシート数を調べる
    private boolean checkSheetNum(int impType, int sheetNum) {
        if (impType == IMPORT_VOD_VTL || impType == IMPORT_VOD_BLD) {
            return sheetNum == 2;
        } else {
            return sheetNum == 1;
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private static final DecimalFormat df = new DecimalFormat("###0.00");

    // 指定されたセルから文字列値を取得する
    // セルのタイプはテキスト｜数値｜日付値だけを想定している
    // 想定外のタイプの場合 "0"を返す。
    private String getStringValue(Cell cell) {
        String rtn = "0";
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            rtn = cell.getStringCellValue();
            break;
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                rtn = sdf.format(cell.getDateCellValue());
            } else {
                rtn = df.format(cell.getNumericCellValue());
            }
            break;
        }
        return rtn;
    }
}
