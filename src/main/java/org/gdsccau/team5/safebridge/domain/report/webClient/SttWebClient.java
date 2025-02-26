package org.gdsccau.team5.safebridge.domain.report.webClient;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SttWebClient {

  private final WebClient client;

  public SttWebClient() {
    Dotenv dotenv = Dotenv.load();

    String apiKeyId = dotenv.get("NAVER_CSR_API_KEY_ID");
    String apiKeySecret = dotenv.get("NAVER_CSR_API_SECRET");
    String baseUrl = "https://naveropenapi.apigw.ntruss.com";

    WebClient client = WebClient
        .builder().baseUrl(baseUrl)
        .defaultHeaders(httpHeaders -> {
          httpHeaders.set("X-NCP-APIGW-API-KEY-ID", apiKeyId);
          httpHeaders.set("X-NCP-APIGW-API-KEY", apiKeySecret);
        })
        .build();

    this.client = client;
  }

  /**
   * @param file stt speech file
   * @return stt text
   */
  public String requestStt(MultipartFile file) throws IOException {
    String uri = "/recog/v1/stt?lang=Kor";
    byte[] fileBytes = file.getBytes();

    return client
        .post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .bodyValue(fileBytes)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

}
