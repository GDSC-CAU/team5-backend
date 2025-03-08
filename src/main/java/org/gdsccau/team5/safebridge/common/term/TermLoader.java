package org.gdsccau.team5.safebridge.common.term;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;

public class TermLoader {

    public static Map<String, String> loadTermsWithMeaning() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new ClassPathResource("terms_with_meaning.json").getFile();
            return objectMapper.readValue(file, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static Set<String> loadTermsOnly() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new ClassPathResource("terms_only.json").getFile();
            List<String> terms = objectMapper.readValue(file, List.class);
            return Set.copyOf(terms);
        } catch (Exception e) {
            e.printStackTrace();
            return Set.of();
        }
    }
}
