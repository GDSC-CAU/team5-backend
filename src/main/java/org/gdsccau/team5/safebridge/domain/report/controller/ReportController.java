package org.gdsccau.team5.safebridge.domain.report.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

  private static final Logger log = LoggerFactory.getLogger(ReportController.class);

  private final ReportService reportService;

  @PostMapping("")

  public ApiResponse<ReportResponseDto> store(@Valid ReportRequestDto requestDto) {

    Report report = this.reportService.store(requestDto);
    return ApiResponse.onSuccess(ReportSuccessCode.REPORT_SUCCESS_CODE,
        ReportConverter.toResponseDto(report));
  }

//  public ApiResponse<>
}
