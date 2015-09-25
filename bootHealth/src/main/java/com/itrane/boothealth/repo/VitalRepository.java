package com.itrane.boothealth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.itrane.boothealth.model.Vital;
import com.itrane.boothealth.model.VitalMst;
import com.itrane.boothealth.model.Vod;

/**
 * バイタル・リポジトリ.
 */
public interface VitalRepository extends JpaRepository<Vital, Long> {
    @Modifying
    @Transactional
    public Long removeByVod(Vod vod);

    @Modifying
    @Transactional
    public Long removeByVitalM(VitalMst vitalMst);
}
