package com.itrane.boothealth.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.Vod;

public interface VodService {

    public List<Vod> findBySokuteiBiBetween(String userName, String sokuteiBi1,
            String sokuteiBi2, Integer syubetu);

    public EntityManager getEm();
    
    public Vod updateVod(Vod e);

    public List<Vod> findPage(int offset, int pageRows,
            UserInfo user, Integer syubetu);
}
