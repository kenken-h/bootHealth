package com.itrane.boothealth.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itrane.boothealth.model.KusuriMst;

/**
 * 薬マスター・リポジトリ.
 */
public interface KusuriMstRepository extends JpaRepository<KusuriMst, Long> {

    /**
     * 薬品名で検索する.薬品名にはユニーク制約を設定しているので、検索結果は１件になる.
     * 
     * @param name
     *            薬品名
     * @return 検索された薬品マスターのリスト
     */
    @Query(value = "select e from KusuriMst e where e.name=:name")
    public List<KusuriMst> findByName(@Param("name") String name);

    /**
     * 薬品マスターの全件を取得する. 薬品名で昇順ソートする.
     * 
     * @return 全薬品マスターのリスト
     */
    @Query("select km from KusuriMst km order by km.name")
    public List<KusuriMst> findAllOrderByName();

    // $$$ ページ取得 $$$$$
    // 検索文字列あり
    /**
     * １ページ分の薬マスターリストを取得する.
     * <p>
     * 検索文字列で薬品名、説明をあいまい検索する。
     * </p>
     * 
     * @param searchStr
     *            検索文字列
     * @param request
     *            ページリクエスト
     * @return 検索された薬マスターリストの１ページ分
     */
    @Query("select km from KusuriMst km where "
            + "concat(km.name, km.setumei) like %:searchStr%")
    public List<KusuriMst> findPage(@Param("searchStr") String searchStr,
            Pageable request);

    // 検索文字列なし
    /**
     * １ページ分の薬マスターリストを取得する.
     * 
     * @param request
     * @return １ページの薬マスターリスト
     */
    @Query("select km from KusuriMst km")
    public List<KusuriMst> findPage(Pageable request);
}
