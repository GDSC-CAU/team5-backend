package org.gdsccau.team5.safebridge.domain.report.service;

import java.io.IOException;
import org.gdsccau.team5.safebridge.common.code.error.ChatErrorCode;
import org.gdsccau.team5.safebridge.common.code.error.ReportErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
import org.gdsccau.team5.safebridge.domain.report.converter.ReportConverter;
import org.gdsccau.team5.safebridge.domain.report.dto.request.ReportRequestDto;
import org.gdsccau.team5.safebridge.domain.report.entity.Report;
import org.gdsccau.team5.safebridge.domain.report.repository.ReportRepository;
import org.gdsccau.team5.safebridge.domain.report.webClient.SttWebClient;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.service.UserCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ReportService {

  private final SttWebClient sttWebClient;
  private final ReportRepository reportRepository;
  private final UserCheckService userCheckService;

  public ReportService(final SttWebClient sttWebClient,
      final ReportRepository reportRepository,
      UserCheckService userCheckService
  ) {
    this.sttWebClient = sttWebClient;
    this.reportRepository = reportRepository;
    this.userCheckService = userCheckService;
  }

  public String store(final ReportRequestDto requestDto) {

    try {
      String text = sttWebClient.requestStt(requestDto.file());
      User user = userCheckService.findByUserId(requestDto.userId());
      User leader = userCheckService.findByUserId(requestDto.leaderId());
      Report report = ReportConverter.toReport(text, user, leader);
      reportRepository.save(report);
      return text;

    } catch (IOException ioException) {
      throw new ExceptionHandler(ReportErrorCode.REPORT_FILE_NOT_CORRECT);
    }

  }
}
