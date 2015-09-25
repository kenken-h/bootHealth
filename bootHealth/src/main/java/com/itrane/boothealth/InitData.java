package com.itrane.boothealth;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.itrane.boothealth.model.KusuriMst;
import com.itrane.boothealth.model.Syohousen;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.repo.KusuriMstRepository;
import com.itrane.boothealth.repo.SyohousenRepository;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;
import com.itrane.boothealth.repo.VodRepository;

/**
 * バイタルマスターと一月分のバイタルデータを作成するビーン。
 * <p>
 * TODO: デモ版のため、ここで1ユーザー分の作成。
 * 実アプリではマスター保守機能を追加する.
 * </p>
 */
@Component
public class InitData {
    
    Logger log = LoggerFactory.getLogger(getClass());

    private VitalMstRepository vmRepo;
    private UserRepository userRepo;
    private KusuriMstRepository kmRepo;
    private SyohousenRepository syohoRepo;
    private VodRepository vodRepo;

    private AppConst def;

    @Autowired
    public InitData(VitalMstRepository vmRepo, UserRepository userRepo,
            VodRepository vodRepo, KusuriMstRepository kmRepo,
            SyohousenRepository syohoRepo, AppConst def) {
        this.vmRepo = vmRepo;
        this.userRepo = userRepo;
        this.kmRepo = kmRepo;
        this.syohoRepo = syohoRepo;
        this.vodRepo = vodRepo;
        this.def = def;
    }

    @PostConstruct
    public void initData() {
        // ここでマスターにデータを設定する
        // TODO: デモ版の仕様。 実際には保守でマスターは作成および更新する。
        // 薬マスターの作成（インポートテストを行えるように作成しない）
        // List<KusuriMst> kms = initKM();
        // ユーザーマスターの作成（１件だけ作成する）
        UserInfo user1 = initUser1();
        // 上記ユーザーのバイタルマスターを作成
        initVm(user1);
        // 上記のユーザーに対する処方箋を作成する
        initSyohosen(user1);

        // VODデータの作成
        createVodData();
    }

    // ユーザーマスターの作成
    public UserInfo initUser1() {
        UserInfo user =
                new UserInfo("user1", "user1", "岡本一郎", "おかもといちろう", "ROLE_ADMIN");
        user.setYubinBango("3720032");
        user.setToDoFuKen("埼玉県");
        user.setSiKuTyoSon("川口市中青木");
        user.setBanTi("1-1-1");
        user.setYakkyoku("東京薬局");
        user.setYakkyokuTel("04522225678");
        user.setYakkyokuFax("04533331234");
        user.setByoin1Mei("東京病院");
        user.setByoin1Jusyo("東京駅");
        user.setByoin1YoyakuTel("031234444");
        user.setByoin2Mei("川崎病院");
        user.setByoin2Jusyo("川崎駅");
        user.setByoin2YoyakuTel("031235555");
        user = userRepo.save(user);
        return user;
    }

    // バイタルマスターの作成
    public void initVm(UserInfo user) {
        // バイタル・マスター
        int type = def.defaultType(); // バイタル
        vmRepo.save(new VitalMst("睡眠", "06:00", 6, 9, "睡眠", 1, type, user));
        vmRepo.save(new VitalMst("体重", "06:00", 58, 62, "体重", 2, type, user));
        vmRepo.save(new VitalMst("空腹時血糖", "06:00", 100, 130, "血糖", 3, type, user));
        vmRepo.save(new VitalMst("朝体温", "06:00", 35.5, 36.9, "体温", 4, type, user));
        vmRepo.save(new VitalMst("午前体温", "10:00", 35.5, 36.9, "体温", 5, type, user));
        vmRepo.save(new VitalMst("午前血圧上", "10:00", 115, 140, "血圧", 6, type, user));
        vmRepo.save(new VitalMst("午前血圧下", "10:00", 60, 90, "血圧", 7, type, user));
        vmRepo.save(new VitalMst("朝食後血糖", "10:30", 115, 136, "血糖", 8, type, user));
        vmRepo.save(new VitalMst("午後体温", "14:00", 35.5, 36.9, "体温", 9, type, user));
        vmRepo.save(new VitalMst("午後血圧上", "14:00", 115, 140, "血圧", 10, type, user));
        vmRepo.save(new VitalMst("午後血圧下", "14:00", 60, 90, "血圧", 11, type, user));
        vmRepo.save(new VitalMst("昼食後血糖", "14:30", 115, 136, "血糖", 12, type, user));
        vmRepo.save(new VitalMst("夕食後血糖", "20:30", 115, 136, "血糖", 13, type, user));
        // 血液検査マスター
        type = def.getSokuteiBlood();
        vmRepo.save(new VitalMst("GOT", "アミノ酸作成酵素", 0, 35, "肝機能", 1, type, user));
        vmRepo.save(new VitalMst("GPT", "", 0, 45, "肝機能", 2, type, user));
        vmRepo.save(new VitalMst("γ-GTP", "アルコール", 0, 50, "肝機能", 3, type, user));
        vmRepo.save(new VitalMst("T-Bill", "ビリルビン ", 0.2, 1.0, "肝機能", 4, type, user));
        vmRepo.save(new VitalMst("ALP", "胆汁うっ滞", 100, 325, "肝機能", 5, type, user));
        vmRepo.save(
                new VitalMst("LDH", "糖分のエネルギー転換酵素", 180, 370, "肝機能", 6, type, user));
        vmRepo.save(
                new VitalMst("Alb", "アルブミン(低=肝障害)", 3.8, 5.3, "肝機能", 7, type, user));
        vmRepo.save(new VitalMst("CRE", "腎臓の濾過機能", 0.6, 1.1, "腎機能", 8, type, user));
    }

    // 処方箋の作成
    public void initSyohosen(UserInfo user) {
        // 処方箋
        syohoRepo.save(new Syohousen("ネオーラルx1, プレドニンx2.5", "08:00", "毎日", "", user));
        syohoRepo.save(new Syohousen("バクタx1", "08:00", "月水金", "", user));
        syohoRepo.save(new Syohousen("ネオーラルx1, ワーファリンx3", "18:30", "毎日", "", user));
    }

    // 薬マスターの作成
    public List<KusuriMst> initKM() {
        List<KusuriMst> kms = new ArrayList<>();
        // 薬マスターの作成
        KusuriMst km = kmRepo.save(new KusuriMst("プレドニン",
                "合成副腎皮質ホルモン製剤であり、1955年、日本国内に初めて導入されたステロイド剤である。"
                        + "ステロイドは、抗炎症作用や免疫抑制作用などの薬理作用を有しており、さまざまな疾患の治療に"
                        + "幅広く用いられている。 炎症の原因に関係なく炎症反応を抑制し、炎症のすべての過程において有効。"
                        + "非常に広範な疾患を抑えるのに有効なので数え切れないほど多くの疾患に適用になり、ほとんどの診療科で"
                        + "使われる。",
                "", "5mg"));
        kms.add(km);
        km = kmRepo.save(new KusuriMst("ネオーラル",
                "Tリンパ球によるインターロイキン2,4,5,13やインターフェロンγなどのサイトカイン転写を"
                        + "特異的かつ可逆的に抑制。臓器移植による拒絶反応の抑制や自己免疫疾患の治療に使用される。"
                        + "副作用として、腎機能障害（腎毒性と呼ばれ、特にクレアチニン値上昇）、高血圧、多毛などがある。",
                "", "25mgカプセル"));
        kms.add(km);
        km = kmRepo.save(
                new KusuriMst("ネキシウム", "適応症 : 胃潰瘍、十二指腸潰瘍、吻合部潰瘍 逆流性食道炎、非ステロイド性抗炎症薬投与時に"
                        + "おける胃潰瘍又は十二指腸潰瘍の再発抑制", "", "20mg"));
        kms.add(km);
        km = kmRepo.save(new KusuriMst("ダイフェン配合錠",
                "ニューモシスチス肺炎（旧：カリニ肺炎）の治療薬として有名であるが、ST合剤には様々な特徴がある。"
                        + " 細菌以外の微生物にも効果がある。 真菌の仲間であるカリニに効果がある以外にも原虫である"
                        + "トキソプラズマなどにも効果がある。 多くのグラム陽性菌、グラム陰性菌に効果がある。",
                "バクタ配合錠", ""));
        kms.add(km);
        km = kmRepo.save(new KusuriMst("ワーファリン",
                "血栓塞栓症の治療及び予防。心臓弁膜症に対する機械弁を用いた弁置換術後や心房細動が原因となる"
                        + "脳塞栓症予防、あるいは深部静脈血栓症による肺塞栓症予防のために、また抗リン脂質抗体症候群での"
                        + "血栓症予防のためにしばしば処方される。",
                "", "1mg"));
        kms.add(km);
        km = kmRepo.save(new KusuriMst("ビソプロロールフマル酸塩",
                "心臓を休ませ、血圧を下げる薬。高血圧症のほか、狭心症や不整脈、慢性心不全の治療に用いる。", "メインテート", "0.625mg"));
        kms.add(km);
        km = kmRepo.save(new KusuriMst("アレンドロン酸",
                "【働き】骨に付着して、骨のカルシウム分が血液に溶け出すのを防ぐ。その結果、骨の密度が増加し"
                        + "骨が丈夫になる。 【薬理】古い骨を壊し血液中に吸収させる役目をするのが“破骨細胞”。"
                        + "この薬は、破骨細胞に特異的に作用し骨吸収を強力に抑制、その結果として骨密度と骨強度を高める。",
                "フォサマック", "35mg"));
        kms.add(km);
        return kms;
    }

    // ###########################
    // VOD のダミーデータを１ヶ月分作成
    // ###########################
    public void createVodData() {
        String[] userNames = { "user1" };
        // ユーザーごとに
        for (String userName : userNames) {
            DateTime todayDt = DateTime.now();
            DateTime startDt = todayDt.minusMonths(1);
            List<UserInfo> pts = userRepo.findByName(userName);
            List<VitalMst> vms = new ArrayList<VitalMst>();
            if (pts.size() == 1) {
                UserInfo user = pts.get(0);
                vms = vmRepo.findAllByUser(user, def.defaultType()); // 種別：バイタル測定値
                int c = 0;
                // システム日の前日から１月前までの期間のバイタル測定を作成
                for (DateTime d = startDt; d.isBefore(todayDt); d = d.plusDays(1)) {
                    List<Vital> vitals = new ArrayList<Vital>();
                    Vod todayVod = new Vod(user, d.toString("yyyy/MM/dd"),
                            def.defaultType());
                    if (vms.size() > 0) {
                        for (VitalMst vm : vms) {
                            if (c >= BS_HOSEI.length) {
                                c = 0;
                            }
                            vitals.add(createVital(vm, todayVod, c));
                            c++;
                        }
                    }
                    todayVod.setVitals(vitals);
                    vodRepo.save(todayVod);
                }
            }
        }
    }

    private double FBS = 110;
    private double BS = 140;
    private double BW = 60;
    private double BT = 36.0;
    private double BPL = 90;
    private double BPH = 135;
    private double SLP = 6;

    private double FBS_HOSEI[] =
            { 20, -5, 25, 30, -10, 50, 10, 10, -20, 35, 40, -5, 25, 30 };
    private double BS_HOSEI[] =
            { 30, 0, 20, 20, -5, 30, 70, 20, 30, -5, 30, 30, 20, 30 };
    private double BW_HOSEI[] =
            { -.2, -.3, -.4, -.5, -.7, -.8, -.8, -.7, -.6, -.4, -.2, .1, .3, .1 };
    private double BT_HOSEI[] =
            { .3, .4, .5, .4, .3, -.2, .3, .7, .8, .6, .5, .8, 1.2, .6 };
    private double BPL_HOSEI[] =
            { 12, 5, 14, 5, 10, -5, 19, 11, 5, 13, -5, 20, 15, 5 };
    private double BPH_HOSEI[] =
            { 19, 25, 13, 30, 20, -10, 28, 20, 25, 15, 30, 20, -10, 22 };
    private double SLP_HOSEI[] =
            { .5, .5, 0, -.5, 0, 0, -.5, 0, 0, -.5, 0, -1, 1, 1.5 };

    private static final DecimalFormat df = new DecimalFormat("###0.00");

    private Vital createVital(VitalMst vm, Vod vod, int c) {
        double val = 0;
        if (vm.getType().equals("睡眠")) {
            val = getVal(SLP, SLP_HOSEI[c]);
        } else if (vm.getType().equals("体重")) {
            val = getVal(BW, BW_HOSEI[c]);
        } else if (vm.getType().equals("血糖")) {
            if (vm.getName().equals("空腹時血糖")) {
                val = getVal(FBS, FBS_HOSEI[c]);
            } else {
                val = getVal(BS, BS_HOSEI[c]);
            }
        } else if (vm.getType().equals("血圧")) {
            if (vm.getName().endsWith("上")) {
                val = getVal(BPH, BPH_HOSEI[c]);
            } else {
                val = getVal(BPL, BPL_HOSEI[c]);
            }
        } else if (vm.getType().equals("体温")) {
            val = getVal(BT, BT_HOSEI[c]);
        }
        return new Vital(vm.getName(), vm.getJikan(), df.format(val), vod, vm);
    }

    private double getVal(double kijun, double hosei) {
        return kijun + hosei;
    }

}
