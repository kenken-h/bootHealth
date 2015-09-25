package com.itrane.boothealth.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.model.Vod;
import com.itrane.boothealth.repo.UserRepository;
import com.itrane.boothealth.repo.VitalRepository;
import com.itrane.boothealth.repo.VodRepository;

@Service
public class VodServiceImpl implements VodService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private VodRepository vodRepository;
    private VitalRepository vitalRepository;
    private UserRepository userRepo;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public VodServiceImpl(VodRepository repo, VitalRepository vitalRepo,
            UserRepository userRepo) {
        this.vodRepository = repo;
        this.vitalRepository = vitalRepo;
        this.userRepo = userRepo;
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    public List<Vod> findBySokuteiBiBetween(String userName, String sokuteiBi1,
            String sokuteiBi2, Integer syubetu) {
        List<UserInfo> pts = userRepo.findByName(userName);
        if (pts.size() == 1) {
            return vodRepository.findBySokuteiBiBetween(pts.get(0), sokuteiBi1,
                    sokuteiBi2, syubetu);
        } else {
            return new ArrayList<Vod>();
        }
    }

    @Override
    @Transactional(rollbackFor = EntityNotFoundException.class)
    public Vod updateVod(Vod e) {
        for (Vital vt : e.getVitals()) {
            if (vt.getSokuteiJikan() != null) {
                vitalRepository.save(vt);
            }
        }
        Vod saveVod = vodRepository.save(e);
        return saveVod;
    }

    @Override
    public List<Vod> findPage(int offset, int pageRows, UserInfo user,
            Integer syubetu) {
        Sort.Direction dir = Sort.Direction.DESC;
        String[] sortCols = { "sokuteiBi" };
        int pageNum = offset / pageRows;
        PageRequest request = new PageRequest(pageNum, pageRows, dir, sortCols);
        List<Vod> vods = vodRepository.findPage(user, syubetu, request);
        Collections.reverse(vods);
        return vods;
    }

}
