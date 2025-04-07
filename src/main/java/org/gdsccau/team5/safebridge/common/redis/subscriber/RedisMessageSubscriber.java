package org.gdsccau.team5.safebridge.common.redis.subscriber;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.term.dto.TermDto.TermIdAndWordDto;
import org.gdsccau.team5.safebridge.domain.term.service.TermCacheCommandService;
import org.gdsccau.team5.safebridge.domain.term.service.TermQueryService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermQueryService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    private final TermQueryService termQueryService;
    private final TranslatedTermQueryService translatedTermQueryService;
    private final TermCacheCommandService termCacheCommandService;
    private final RedisManager redisManager;

    @Async("threadPoolTaskExecutor")
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Redis Subscribe !!");
        Map<String, Double> hotTermMap = calculateHotTerm();
        Map<String, List<Language>> wordLanguageMap = getWordLanguageMap(hotTermMap);
        List<TermIdAndWordDto> termIdAndWordDtos = getAllTermIdAndWords(hotTermMap);
        updateHotTermInLocalCache(wordLanguageMap, termIdAndWordDtos);
        log.info("Hot Term Warming !!");
    }

    private Map<String, Double> calculateHotTerm() {
        LocalDateTime currentTime = LocalDateTime.now();
        Map<String, Double> totalScoreMap = initTotalScoreMap(currentTime);
        calculateTermFindCount(totalScoreMap, currentTime);
        return getTop100Term(totalScoreMap);
    }

    private Map<String, Double> getTermFindTimeMemberWithScore(final LocalDateTime currentTime) {
        Map<String, Double> map = new HashMap<>();
        redisManager.getTermFindTimeZSet(currentTime).forEach(tuple -> {
            String member = tuple.getValue();
            Double score = tuple.getScore();
            map.put(member, score);
        });
        return map;
    }

    private Map<String, Double> initTotalScoreMap(final LocalDateTime currentTime) {
        double findTimeWeight = 0.01; // TODO 몇으로 해야할까?
        Map<String, Double> termFindTimeMap = getTermFindTimeMemberWithScore(currentTime);
        Map<String, Double> totalScoreMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : termFindTimeMap.entrySet()) {
            String field = entry.getKey();
            Double lastFindTimeScore = entry.getValue();
            totalScoreMap.putIfAbsent(field, lastFindTimeScore * findTimeWeight);
        }
        return totalScoreMap;
    }

    private void calculateTermFindCount(Map<String, Double> totalScoreMap, final LocalDateTime currentTime) {
        int a = 1;
        double r = 0.1;

        for (int i = 0; i < 24; i++) {
            LocalDateTime hourTime = currentTime.minusHours(i);
            Map<Object, Object> findCountHash = redisManager.getTermFindCount(hourTime);

            for (Map.Entry<Object, Object> entry : findCountHash.entrySet()) {
                String field = entry.getKey().toString();
                Integer count = Integer.parseInt(entry.getValue().toString());
                Double countWeight = a * Math.pow(1 - r, i);
                Double findCountScore = countWeight * count;
                totalScoreMap.computeIfPresent(field, (k, v) -> v + findCountScore);
            }
        }
    }

    private Map<String, Double> getTop100Term(Map<String, Double> originalMap) {
        return originalMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(100)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private Map<String, List<Language>> getWordLanguageMap(final Map<String, Double> map) {
        Map<String, List<Language>> wordMap = new HashMap<>();
        map.forEach((k, v) -> {
            String word = k.split(":")[0];
            Language language = Language.valueOf(k.split(":")[1]);
            wordMap.computeIfAbsent(word, key -> new ArrayList<>()).add(language);
        });
        return wordMap;
    }

    private List<String> getAllWords(final Map<String, Double> map) {
        List<String> words = new ArrayList<>();
        map.forEach((k, v) -> {
            String word = k.split(":")[0];
            words.add(word);
        });
        return words;
    }

    private List<TermIdAndWordDto> getAllTermIdAndWords(final Map<String, Double> map) {
        List<String> words = getAllWords(map);
        return termQueryService.findTermIdAndWord(words);
    }

    private void updateHotTermInLocalCache(final Map<String, List<Language>> wordLanguageMap,
                                           final List<TermIdAndWordDto> termIdAndWordDtos) {
        // TODO IN 절로 쿼리 1번에 다 가져오고 싶은데, 쉽지 않네;
        termIdAndWordDtos.forEach(dto -> {
            Long termId = dto.getTermId();
            String word = dto.getWord();
            List<Language> languages = wordLanguageMap.get(word);
            translatedTermQueryService.findTranslatedWordsByLanguagesAndTermId(languages, termId).forEach(data -> {
                String translatedWord = data.getTranslatedWord();
                Language language = data.getLanguage();
                termCacheCommandService.updateTerm(word, language, translatedWord);
            });
        });
    }
}
