package com.monitoring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monitoring.service.CpuUsageService;

@RestController
@RequestMapping("/api/cpu-usage")
public class CpuUsageController {

	@Autowired
    private CpuUsageService cpuUsageService;

	
	
}
