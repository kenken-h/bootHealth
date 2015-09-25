package com.itrane.boothealth.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itrane.boothealth.AppUtil;
import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalMstRepository;
import com.itrane.boothealth.repo.VitalRepository;

@Service
public class VitalMstServiceImpl implements VitalMstService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private VitalMstRepository repo;
    private UserRepository userRepo;
    private VitalRepository vitalRepo;

    /**
     * コンストラクタ.
     * 
     * @param repo
     */
    @Autowired
    public VitalMstServiceImpl(VitalMstRepository repo, UserRepository userRepo,
            VitalRepository vitalRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.vitalRepo = vitalRepo;
    }

    // #############
    // Page 取得
    // #############
    @Override
    public List<VitalMst> findByPage(UserInfo user, int syubetu, int offset,
            int pageRows, String searchStr, String sortDir, String... cols) {
        Sort.Direction dir = Sort.Direction.ASC;
        if (sortDir.equals("desc")) {
            dir = Sort.Direction.DESC;
        }
        int pageNum = offset / pageRows;
        PageRequest request = new PageRequest(pageNum, pageRows, dir, cols);

        if (searchStr.equals("")) {
            return repo.findPage(user, syubetu, request);
        } else {
            return repo.findPage(user, syubetu, searchStr, request);
        }
    }

    // #############
    // CRUD
    // #############
    @Override
    public List<VitalMst> findByUserName(String userName, int syubetu) {
        UserInfo user = AppUtil.getUser(userRepo, userName);
        if (user != null) {
            return repo.findAllByUser(user, syubetu);
        } else {
            return new ArrayList<VitalMst>();
        }
    }

    @Override
    @Transactional
    public void delete(VitalMst e) {
        vitalRepo.removeByVitalM(e);
        repo.delete(e);
    }

    @Override
    @Transactional
    public VitalMst save(VitalMst e)
            throws ConstraintViolationException, DbAccessException {
        VitalMst p = null;
        try {
            p = repo.save(e);
        } catch (Exception ex) {
            throw new DbAccessException("DBエラー", DbAccessException.OTHER_ERROR);
        }
        return p;
    }
}