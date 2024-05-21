package com.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monitoring.entity.CpuUsageEntity;

@Repository
public interface CpuUsageRepository extends JpaRepository<CpuUsageEntity, Long> {

}
