package com.itrane.boothealth.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.repo.VitalRepository;

@Service
public class VitalServiceImpl implements VitalService {

    @Resource
    private VitalRepository vitalRepository;

    public VitalServiceImpl() {
    }

    public VitalServiceImpl(VitalRepository repo) {
        this.vitalRepository = repo;
    }

    @Override
    public Vital findById(long id) {
        return vitalRepository.findOne(id);
    }

}
