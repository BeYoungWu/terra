package com.monitoring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monitoring.repository.CpuUsageRepository;

@Service
public class CpuUsageService {

	@Autowired
	private CpuUsageRepository cpuUsageRepository;
	
}
