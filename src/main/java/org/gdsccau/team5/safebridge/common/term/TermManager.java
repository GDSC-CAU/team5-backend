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
import java.util.HashMap;
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
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataWithNewChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TranslatedDataDto;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.term.service.TermCheckService;
import org.gdsccau.team5.safebridge.domain.term.service.TermService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermCheckService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermService;
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

    private final RedisManager redisManager;
    private final TermCheckService termCheckService;
    private final TermService termService;
    private final TranslatedTermCheckService translatedTermCheckService;
    private final TranslatedTermService translatedTermService;

    private final Trie trie;
    private final Map<String, String> termsWithMeaning;

    public TermManager(final RedisManager redisManager, final TermCheckService termCheckService,
                       final TermService termService, final TranslatedTermCheckService translatedTermCheckService,
                       final TranslatedTermService translatedTermService) {
        Set<String> terms = TermLoader.loadTermsOnly();
        TrieBuilder builder = Trie.builder();
        for (String term : terms) {
            builder.addKeyword(term);
        }
        this.trie = builder.build();
        this.termsWithMeaning = TermLoader.loadTermsWithMeaning();
        this.redisManager = redisManager;
        this.termCheckService = termCheckService;
        this.termService = termService;
        this.translatedTermCheckService = translatedTermCheckService;
        this.translatedTermService = translatedTermService;
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
    public CompletableFuture<TranslatedDataDto> translate(final String text, final List<TermDataDto> termDataDtos,
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
                Map<String, String> translatedTerms = this.translateTerms(translate, termDataDtos, language);
                String translatedText = translation.getTranslatedText().replaceAll("&#39;", "'");
                return this.createdTranslatedDataDto(translatedText, translatedTerms);
            } catch (IOException e) {
                log.error("Google Translate API 호출 중 오류 발생", e);
                return this.createdTranslatedDataDto(null, null);
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

    private Map<String, String> translateTerms(final Translate translate, final List<TermDataDto> termDataDtos,
                                               final Language language) {
        Map<String, String> result = new HashMap<>();
        termDataDtos.forEach(termDataDto -> {
            // 1. Term을 가져온다.
            Term term = termCheckService.findTermByWord(termDataDto.getTerm());
            if (term == null) {
                term = termService.createTerm(termDataDto.getTerm());
            }
            // 2. Redis에서 번역본을 가져온다.
            String translatedTermKey = redisManager.getTranslatedTermKey(term.getId(), language);
            String translatedTerm =  redisManager.getTranslatedTerm(translatedTermKey);
            // 3. Redis에 없으면 DB에서 가져온다. -> 락을 활용해 같은 요청에 대해선 락을 획득한 요청만 DB를 조회한다.
            if (translatedTerm == null) {
                translatedTerm = translatedTermCheckService.findTranslatedTermByLanguageAndTermId(language, term.getId());
            }
            // 4. DB에도 없으면 번역 API를 호출한다. (초기 1회, 불가피)
            if (translatedTerm == null) {
                Translation translation = translate.translate(term.getWord(),
                        Translate.TranslateOption.sourceLanguage(SOURCE_LANGUAGE_CODE),
                        Translate.TranslateOption.targetLanguage(language.getCode()));
                translatedTerm = translation.getTranslatedText().replaceAll("&#39;", "'");
                // 5. 번역본을 DB에 저장한다.
                translatedTermService.createTranslatedTerm(term, language, translatedTerm);
            }
            // 6. Redis에 저장한다. 이 때, 이미 저장되어 있다면 TTL을 갱신한다. (Write-Through)
            redisManager.updateTranslatedTerm(translatedTermKey, translatedTerm);
            result.put(term.getWord(), translatedTerm);
        });
        return result;
    }

    private TranslatedDataDto createdTranslatedDataDto(final String translatedText,
                                                       final Map<String, String> translatedTerms) {
        return TranslatedDataDto.builder()
                .translatedText(translatedText)
                .translatedTerms(translatedTerms)
                .build();
    }
}
