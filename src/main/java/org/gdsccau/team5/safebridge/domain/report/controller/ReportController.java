package org.gdsccau.team5.safebridge.domain.report.controller;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.gdsccau.team5.safebridge.common.code.success.TestSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/report")
public class ReportController {

  private static final Logger log = LoggerFactory.getLogger(ReportController.class);

  @PostMapping("")
  public ApiResponse<Void> store(MultipartFile file, HttpServletRequest request) {

    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("data", file.getResource());
    Dotenv dotenv = Dotenv.load();

    String apiKeyId = dotenv.get("NAVER_CSR_API_KEY_ID");
    String apiKeySecret = dotenv.get("NAVER_CSR_API_SECRET");

    WebClient client = WebClient
        .builder().baseUrl("https://naveropenapi.apigw.ntruss.com")
        .defaultHeaders(httpHeaders -> {
          httpHeaders.set("X-NCP-APIGW-API-KEY-ID", apiKeyId);
          httpHeaders.set("X-NCP-APIGW-API-KEY", apiKeySecret);
        })
        .build();
    String uri = "/recog/v1/stt?lang=Kor";

    try {
      byte[] fileBytes = file.getBytes();
      String json = client
          .post()
          .uri(uri)
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .bodyValue(fileBytes)
          .exchange()
          .block()
          .bodyToMono(String.class)
          .block();
      log.info(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ApiResponse.onSuccess(TestSuccessCode.TEST_SUCCESS);
  }
}
