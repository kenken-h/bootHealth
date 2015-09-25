package com.itrane.boothealth.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.VitalMst;

/**
 * バイタルマスター・リポジトリ.
 */
public interface VitalMstRepository extends JpaRepository<VitalMst, Long> {

    // クエリー
    @Query("select v from VitalMst v where v.user=:user and v.syubetu=:syubetu"
            + " and v.name=:name")
    public List<VitalMst> findByUserAndName(@Param("user") UserInfo user,
            @Param("syubetu") int syubetu, @Param("name") String name);

    @Query("select v from VitalMst v where v.user=:user and v.syubetu=:syubetu")
    public List<VitalMst> findAllByUser(@Param("user") UserInfo user,
            @Param("syubetu") int syubetu);

    @Query("select v from VitalMst v where v.user=:user and v.syubetu=:syubetu"
            + " and concat(v.name, v.jikan) like %:searchStr%")
    public List<VitalMst> findPage(@Param("user") UserInfo user,
            @Param("syubetu") int syubetu,
            @Param("searchStr") String searchStr, Pageable pr);

    @Query("select v from VitalMst v where v.user=:user and v.syubetu=:syubetu")
    public List<VitalMst> findPage(@Param("user") UserInfo user,
            @Param("syubetu") int syubetu, Pageable pr);

    @Query("select count(v) from VitalMst v where v.user=:user and v.syubetu=:syubetu")
    public int countByUser(@Param("user") UserInfo user,
            @Param("syubetu") int syubetu);

    public Long removeByUser(String userName);
}
