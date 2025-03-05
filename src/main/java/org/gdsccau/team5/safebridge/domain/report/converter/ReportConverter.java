package org.gdsccau.team5.safebridge.domain.report.converter;

import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.report.dto.response.ReportResponseDto;
import org.gdsccau.team5.safebridge.domain.report.entity.Report;
import org.gdsccau.team5.safebridge.domain.user.entity.User;

public class ReportConverter {

  public static Report toReport(final String text, final User user, final User leader) {
    return Report.builder()
        .text(text)
        .user(user)
        .leader(leader)
        .build();
  }

  public static ReportResponseDto toResponseDto(Report report) {
    return new ReportResponseDto(report.getId(),
        Optional.ofNullable(report.getUser()).map(User::getId).orElse(null),
        Optional.ofNullable(report.getLeader()).map(User::getId).orElse(null),
        report.getText(),
        report.getCreatedAt());
  }

}
