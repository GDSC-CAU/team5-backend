package org.gdsccau.team5.safebridge.common.term;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;

public class TermLoader {

    public static Map<String, String> loadTermsWithMeaning() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            try (InputStream inputStream = new ClassPathResource("terms_with_meaning.json").getInputStream()) {
                return objectMapper.readValue(inputStream, HashMap.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static Set<String> loadTermsOnly() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            try (InputStream inputStream = new ClassPathResource("terms_only.json").getInputStream()) {
                List<String> terms = objectMapper.readValue(inputStream, List.class);
                return Set.copyOf(terms);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Set.of();
        }
    }
}
