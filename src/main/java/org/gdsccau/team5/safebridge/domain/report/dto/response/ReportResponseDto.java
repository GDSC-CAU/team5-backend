package org.gdsccau.team5.safebridge.domain.report.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public record ReportResponseDto(
    Long reportId,
    Long userId,
    Long leaderId,
    String text,
    LocalDateTime dateTime
) {

}
