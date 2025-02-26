package org.gdsccau.team5.safebridge.domain.report.converter;

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

}
