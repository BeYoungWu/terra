package com.monitoring.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.monitoring.entity.CpuUsage;


@Repository
public interface CpuUsageRepository extends JpaRepository<CpuUsage, Long> {

	// CPU 사용률 분 단위 조회 (최근 1주 데이터 제공) 
	@Query("SELECT u FROM tbl_usage u WHERE u.timestamp BETWEEN :start AND :end AND u.timestamp >= :weekAgo ORDER BY u.timestamp")
	List<CpuUsage> getCpuUsagePerMinute(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, @Param("weekAgo")LocalDateTime weekAgo);

}
