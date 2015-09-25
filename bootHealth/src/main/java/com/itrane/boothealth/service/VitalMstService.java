package com.itrane.boothealth.service;

import java.util.List;

import javax.validation.ConstraintViolationException;

import com.itrane.boothealth.model.UserInfo;
import com.itrane.boothealth.model.VitalMst;

public interface VitalMstService {

    public VitalMst save(VitalMst e) throws ConstraintViolationException,
            DbAccessException;

    public void delete(VitalMst e);

    public List<VitalMst> findByPage(UserInfo user, int syubetu, int offset,
            int pageRows, String searchStr, String sortDir, String... cols);

    public List<VitalMst> findByUserName(String userName, int syubtu);
}
