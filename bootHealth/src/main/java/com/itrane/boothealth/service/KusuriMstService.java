package com.itrane.boothealth.service;

import java.util.List;

import javax.validation.ConstraintViolationException;

import com.itrane.boothealth.model.KusuriMst;

/**
 * 薬マスター・サービス。
 */
public interface KusuriMstService {

    public KusuriMst save(KusuriMst e) throws ConstraintViolationException,
            DbAccessException;

    public void delete(KusuriMst e);

    public List<KusuriMst> findByPage(int offset, int pageRows,
            String searchStr, String sortDir, String... sortCols);
}
