package com.itrane.boothealth.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.Vod;

/**
 * VODリポジトリ.
 */
public interface VodRepository extends JpaRepository<Vod, Long> {

    // %%% バイタル測定 と 血液検査 %%%%%%%%%
    @Query("select v from Vod v where v.user=:user  and v.syubetu=:syubetu and "
            + "v.sokuteiBi=:sokuteiBi order by v.sokuteiBi")
    public List<Vod> findBySokuteiBi(@Param("user") UserInfo user,
            @Param("sokuteiBi") String sokuteiBi,
            @Param("syubetu") Integer syubetu);

    @Query("select v from Vod v where v.user=:user and v.syubetu=:syubetu and (v.sokuteiBi "
            + "between :sokuteiBi1 and :sokuteiBi2) order by v.sokuteiBi")
    public List<Vod> findBySokuteiBiBetween(@Param("user") UserInfo user,
            @Param("sokuteiBi1") String sokuteiBi1,
            @Param("sokuteiBi2") String sokuteiBi2,
            @Param("syubetu") Integer syubetu);

    /**
     * バイタル｜血液検査データの指定ユーザー、指定種別のデータ全件を取得する.
     * 測定値が全て０のデータは無効なデータとして除外する.
     * @param user ユーザー
     * @param syubetu 種別（バイタル｜血液検査）
     * @return　データ数
     */
    @Query("select v from Vod v join Vital vt on vt.vod.id=v.id "
            + "where v.user=:user and v.syubetu=:syubetu "
            + "and vt.sokuteiTi>0 group by v.id　order by v.sokuteiBi")
    public List<Vod> findAllByUser(@Param("user") UserInfo user,
            @Param("syubetu") Integer syubetu);
    
    //#### page #####
    /**
     * バイタル一覧｜血液検査一覧の１ページ分のデータを取得する.
     * 測定値が全て０のデータは無効なデータとして除外する.
     * ソート順はページリクエストで行う.
     * @param user ユーザー
     * @param syubetu 種別（バイタル｜血液検査）
     * @param request
     * @return　Vod リスト
     */
    @Query("select v from Vod v join Vital vt on vt.vod.id=v.id "
            + "where v.user=:user and v.syubetu=:syubetu "
            + "and vt.sokuteiTi>0 group by v.id")
    public List<Vod> findPage(@Param("user") UserInfo user,
            @Param("syubetu") Integer syubetu, Pageable request);

    /**
     * バイタル｜血液検査データの指定ユーザー、指定種別の件数を取得する.
     * 測定値が全て０のデータは無効なデータとして除外する.
     * @param user ユーザー
     * @param syubetu 種別（バイタル｜血液検査）
     * @return　データ数
     */
    @Query("select count(distinct v.id) from Vod v join Vital vt on vt.vod.id=v.id "
            + "where v.user=:user and v.syubetu=:syubetu and vt.sokuteiTi>0")
    public int countByUserAndSyubetu(@Param("user") UserInfo user,
            @Param("syubetu") Integer syubetu);
}
