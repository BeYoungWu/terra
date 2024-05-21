package com.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monitoring.entity.CpuUsage;


@Repository
public interface CpuUsageRepository extends JpaRepository<CpuUsage, Long> {

}
