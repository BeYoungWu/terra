package com.monitoring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.monitoring.dto.CpuUsageAnalysis;
import com.monitoring.entity.CpuUsage;
import com.monitoring.service.CpuUsageService;

@WebMvcTest(CpuUsageController.class)
public class CpuUsageControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CpuUsageService cpuUsageService;
	
	@Test
    @DisplayName("POST CPU 사용률 수집 컨트롤러 로직 확인")
    public void postCollectAndSaveCpuUsageTest() throws Exception {
        // Given
        Mockito.doNothing().when(cpuUsageService).collectAndSaveCpuUsage();

        // When / Then
        mockMvc.perform(post("/api/cpu-usage"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET CPU 사용률 분 단위 조회 컨트롤러 로직 확인")
    public void getCpuUsagePerMinuteTest() throws Exception {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 5, 24, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 5, 24, 23, 59, 59);
        List<CpuUsage> cpuUsages = new ArrayList<>();
        CpuUsage usage1 = new CpuUsage(1L, LocalDateTime.of(2024, 5, 24, 12, 0), 30.0);
        CpuUsage usage2 = new CpuUsage(2L, LocalDateTime.of(2024, 5, 24, 12, 1), 40.0);
        cpuUsages.add(usage1);
        cpuUsages.add(usage2);
        Mockito.when(cpuUsageService.getCpuUsagePerMinute(start, end)).thenReturn(cpuUsages);

        // When / Then
        mockMvc.perform(get("/api/cpu-usage/minute")
                .param("start", start.toString())
                .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].timestamp").value("2024-05-24T12:00:00"))
                .andExpect(jsonPath("$[0].usage").value(30.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].timestamp").value("2024-05-24T12:01:00"))
                .andExpect(jsonPath("$[1].usage").value(40.0));
    }

    @Test
    @DisplayName("GET CPU 사용률 시 단위 조회 컨트롤러 로직 확인")
    public void getCpuUsagePerHourTest() throws Exception {
        // Given
        String day = "2024-05-24";
        List<CpuUsageAnalysis> cpuUsageAnalysisList = new ArrayList<>();
        CpuUsageAnalysis analysis1 = new CpuUsageAnalysis(0, 0, 0, 12, 10.0, 80.0, 45.0);
        CpuUsageAnalysis analysis2 = new CpuUsageAnalysis(0, 0, 0, 13, 15.0, 85.0, 50.0);
        cpuUsageAnalysisList.add(analysis1);
        cpuUsageAnalysisList.add(analysis2);
        Mockito.when(cpuUsageService.getCpuUsagePerHour(day)).thenReturn(cpuUsageAnalysisList);

        // When / Then
        mockMvc.perform(get("/api/cpu-usage/hour")
                .param("day", day))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].hour").value(12))
                .andExpect(jsonPath("$[0].minUsage").value(10.0))
                .andExpect(jsonPath("$[0].maxUsage").value(80.0))
                .andExpect(jsonPath("$[0].avgUsage").value(45.0))
                .andExpect(jsonPath("$[1].hour").value(13))
                .andExpect(jsonPath("$[1].minUsage").value(15.0))
                .andExpect(jsonPath("$[1].maxUsage").value(85.0))
                .andExpect(jsonPath("$[1].avgUsage").value(50.0));
    }

    @Test
    @DisplayName("GET CPU 사용률 일 단위 조회 컨트롤러 로직 확인")
    public void getCpuUsagePerDayTest() throws Exception {
        // Given
        String start = "2024-05-01";
        String end = "2024-05-31";
        List<CpuUsageAnalysis> cpuUsageAnalysisList = new ArrayList<>();
        CpuUsageAnalysis analysis1 = new CpuUsageAnalysis(2024, 5, 24, 0, 10.0, 80.0, 45.0);
        CpuUsageAnalysis analysis2 = new CpuUsageAnalysis(2024, 5, 25, 0, 15.0, 85.0, 50.0);
        cpuUsageAnalysisList.add(analysis1);
        cpuUsageAnalysisList.add(analysis2);
        Mockito.when(cpuUsageService.getCpuUsagePerDay(start, end)).thenReturn(cpuUsageAnalysisList);

        // When / Then
        mockMvc.perform(get("/api/cpu-usage/day")
                .param("start", start)
                .param("end", end))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].year").value(2024))
                .andExpect(jsonPath("$[0].month").value(5))
                .andExpect(jsonPath("$[0].day").value(24))
                .andExpect(jsonPath("$[0].minUsage").value(10.0))
                .andExpect(jsonPath("$[0].maxUsage").value(80.0))
                .andExpect(jsonPath("$[0].avgUsage").value(45.0))
                .andExpect(jsonPath("$[1].year").value(2024))
                .andExpect(jsonPath("$[1].month").value(5))
                .andExpect(jsonPath("$[1].day").value(25))
                .andExpect(jsonPath("$[1].minUsage").value(15.0))
                .andExpect(jsonPath("$[1].maxUsage").value(85.0))
                .andExpect(jsonPath("$[1].avgUsage").value(50.0));
    }
	
}
