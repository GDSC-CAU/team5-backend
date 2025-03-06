package org.gdsccau.team5.safebridge.common.term;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataWithNewChatDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TermManager {

    @Value("${google.service-account.type}")
    private String type;
    @Value("${google.service-account.project_id}")
    private String projectId;
    @Value("${google.service-account.private_key_id}")
    private String privateKeyId;
    @Value("${google.service-account.private_key}")
    private String privateKey;
    @Value("${google.service-account.client_email}")
    private String clientEmail;
    @Value("${google.service-account.client_id}")
    private String clientId;
    @Value("${google.service-account.auth_uri}")
    private String authUri;
    @Value("${google.service-account.token_uri}")
    private String tokenUri;
    @Value("${google.service-account.auth_provider_x509_cert_url}")
    private String authProviderX509CertUrl;
    @Value("${google.service-account.client_x509_cert_url}")
    private String clientX509CertUrl;
    @Value("${google.service-account.universe_domain}")
    private String universeDomain;

    private static final String SOURCE_LANGUAGE_CODE = "ko";

    private Trie trie;
    private Map<String, String> termsWithMeaning;

    public TermManager() {
        Set<String> terms = TermLoader.loadTermsOnly();
        TrieBuilder builder = Trie.builder();
        for (String term : terms) {
            builder.addKeyword(term);
        }
        this.trie = builder.build();
        this.termsWithMeaning = TermLoader.loadTermsWithMeaning();
    }

    public TermDataWithNewChatDto query(final String chat) {
        Collection<Emit> emits = this.trie.parseText(chat);
        Set<String> terms = emits.stream()
                .map(Emit::getKeyword)
                .collect(Collectors.toSet());
        Set<String> finalTerms = new HashSet<>(terms);

        this.removeDuplicatedWord(terms, finalTerms);
        String newChat = this.convertOriginalTerms(finalTerms, emits, chat);

        return TermDataWithNewChatDto.builder()
                .terms(emits.stream()
                        .filter(emit -> finalTerms.contains(emit.getKeyword()))
                        .map(emit -> TermDataDto.builder()
                                .startIndex(emit.getStart())
                                .endIndex(emit.getEnd())
                                .term(emit.getKeyword())
                                .meaning(this.termsWithMeaning.get(emit.getKeyword()))
                                .build())
                        .toList())
                .newChat(newChat)
                .build();
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String> translate(final String text, final List<TermDataDto> terms,
                                               final Language language) {
        return CompletableFuture.supplyAsync(() -> {
            try (InputStream inputStream = getCredentialsStream()) {
                GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream);
                Translate translate = TranslateOptions.newBuilder()
                        .setCredentials(credentials)
                        .build()
                        .getService();
                Translation translation = translate.translate(text,
                        Translate.TranslateOption.sourceLanguage(SOURCE_LANGUAGE_CODE),
                        Translate.TranslateOption.targetLanguage(language.getCode()));
                return translation.getTranslatedText().replaceAll("&#39;", "'");
            } catch (IOException e) {
                log.error("Google Translate API 호출 중 오류 발생", e);
                return "";
            }
        });
    }

    private void removeDuplicatedWord(final Set<String> terms, Set<String> finalTerms) {
        Set<String> toRemove = new HashSet<>();
        for (String term : terms) {
            for (String otherTerm : terms) {
                if (!term.equals(otherTerm) && otherTerm.contains(term)) {
                    toRemove.add(term);
                }
            }
        }
        finalTerms.removeAll(toRemove);
    }

    private String convertOriginalTerms(final Set<String> finalTerms, final Collection<Emit> emits, final String chat) {
        StringBuilder newChat = new StringBuilder(chat);

        List<Emit> sortedEmits = emits.stream()
                .filter(emit -> finalTerms.contains(emit.getKeyword()))
                .sorted((a, b) -> Integer.compare(b.getStart(), a.getStart()))
                .toList();

        sortedEmits.forEach(emit -> {
            int startIndex = emit.getStart();
            int endIndex = emit.getEnd() + 1;
            String term = emit.getKeyword();
            String meaning = this.termsWithMeaning.get(term).split(",")[0];
            newChat.replace(startIndex, endIndex, meaning);
        });

        return newChat.toString();
    }

    private InputStream getCredentialsStream() {
        String jsonContent = String.format("""
                        {
                          "type": "%s",
                          "project_id": "%s",
                          "private_key_id": "%s",
                          "private_key": "%s",
                          "client_email": "%s",
                          "client_id": "%s",
                          "auth_uri": "%s",
                          "token_uri": "%s",
                          "auth_provider_x509_cert_url": "%s",
                          "client_x509_cert_url": "%s",
                          "universe_domain": "%s"
                        }
                        """, type, projectId, privateKeyId, privateKey, clientEmail, clientId, authUri,
                tokenUri, authProviderX509CertUrl, clientX509CertUrl, universeDomain);
        return new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));
    }
}
