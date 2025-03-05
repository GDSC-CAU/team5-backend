package org.gdsccau.team5.safebridge.domain.report.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.ReportSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.report.converter.ReportConverter;
import org.gdsccau.team5.safebridge.domain.report.dto.request.ReportRequestDto;
import org.gdsccau.team5.safebridge.domain.report.dto.response.ReportResponseDto;
import org.gdsccau.team5.safebridge.domain.report.entity.Report;
import org.gdsccau.team5.safebridge.domain.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("")
  public ApiResponse<ReportResponseDto> store(@Valid ReportRequestDto requestDto) {

    Report report = this.reportService.store(requestDto);
    return ApiResponse.onSuccess(ReportSuccessCode.REPORT_SUCCESS_CODE,
        ReportConverter.toResponseDto(report));
  }

  @GetMapping("/{leaderId}")
  public ApiResponse<List<ReportResponseDto>> list(@PathVariable("leaderId") Long leaderId) {

    // TODO: leader User 데이터 확인 및 권한 확인
    List<Report> reports = this.reportService.list(leaderId);

    return ApiResponse.onSuccess(ReportSuccessCode.REPORT_LIST_SUCCESS_CODE,
        ReportResponseDto.fromList(reports));
  }
}
