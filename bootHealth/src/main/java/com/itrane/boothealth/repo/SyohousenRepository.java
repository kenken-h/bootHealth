package com.itrane.boothealth.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itrane.boothealth.model.Syohousen;

/**
 * 処方箋リポジトリ.
 */
public interface SyohousenRepository extends JpaRepository<Syohousen, Long> {

}
