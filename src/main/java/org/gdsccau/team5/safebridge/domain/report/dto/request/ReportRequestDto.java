package org.gdsccau.team5.safebridge.domain.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ReportRequestDto(
    @NotNull(message = "미디어 파일은 필수입니다.") MultipartFile file,
    @NotNull(message = "user Id는 필수입니다.") Long userId,
    @NotNull(message = "admin Id는 필수입니다.") Long adminId
) {

}
