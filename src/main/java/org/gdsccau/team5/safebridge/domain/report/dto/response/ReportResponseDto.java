package org.gdsccau.team5.safebridge.domain.report.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.gdsccau.team5.safebridge.domain.report.entity.Report;
import org.springframework.web.multipart.MultipartFile;

public record ReportResponseDto(
    Long reportId,
    Long userId,
    Long leaderId,
    String text,
    LocalDateTime dateTime
) {
  // 개별 Report 변환용
  public static ReportResponseDto from(Report report) {
    return new ReportResponseDto(
        report.getId(),
        report.getUser() != null ? report.getUser().getId() : null,
        report.getLeader() != null ? report.getLeader().getId() : null,
        report.getText(),
        report.getCreatedAt()
    );
  }

  // 리스트 변환용
  public static List<ReportResponseDto> fromList(List<Report> reports) {
    return reports.stream()
        .map(ReportResponseDto::from)
        .toList();
  }
}
