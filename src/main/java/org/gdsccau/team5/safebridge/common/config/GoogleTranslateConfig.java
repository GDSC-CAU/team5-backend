package org.gdsccau.team5.safebridge.common.config;
import com.google.cloud.translate.Translate;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.TranslateOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleTranslateConfig {

    @Value("${google.credentials.path}")
    private String credentialsPath;

    @Bean
    public Translate googleTranslate() throws IOException {
        try (InputStream inputStream = new FileInputStream(credentialsPath)) {
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream);
            return TranslateOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        }
    }
}
