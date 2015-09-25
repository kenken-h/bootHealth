package com.itrane.boothealth.service;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.itrane.boothealth.model.KusuriMst;
import com.itrane.boothealth.repo.KusuriMstRepository;

@Service
public class KusuriMstServiceImpl implements KusuriMstService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private KusuriMstRepository repo;

    @Autowired
    public KusuriMstServiceImpl(KusuriMstRepository repo) {
        this.repo = repo;
    }

    // #############
    // Page 取得
    // #############
    @Override
    public List<KusuriMst> findByPage(int offset, int pageRows,
            String searchStr, String sortDir, String... sortCols) {
        Assert.notNull(searchStr, "searchStr が null.");
        Assert.notNull(sortDir, "sortDir が null.");
        Assert.notNull(sortCols, "sortCols が null.");

        Sort.Direction dir = Sort.Direction.ASC;
        if (sortDir.equals("desc")) {
            dir = Sort.Direction.DESC;
        }
        int pageNum = offset / pageRows;
        PageRequest request = new PageRequest(pageNum, pageRows, dir, sortCols);

        if (searchStr.equals("")) {
            return repo.findPage(request);
        } else {
            return repo.findPage(searchStr, request);
        }
    }

    // #############
    // CRUD
    // #############
    // @Override
    @Transactional
    public KusuriMst save(KusuriMst e)
            throws ConstraintViolationException, DbAccessException {
        Assert.notNull(e, "KusuriMst e が null.");

        KusuriMst p = null;
        try {
            p = repo.save(e);
        } catch (Exception ex) {
            throw new DbAccessException("DBエラー", DbAccessException.OTHER_ERROR);
        }
        return p;
    }

    @Override
    @Transactional
    public void delete(KusuriMst e) {
        Assert.notNull(e, "KusuriMst e が null.");

        repo.delete(e);
    }
}
