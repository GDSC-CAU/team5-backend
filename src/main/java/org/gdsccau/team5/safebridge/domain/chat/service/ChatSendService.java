package org.gdsccau.team5.safebridge.domain.chat.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.common.term.TermManager;
import org.gdsccau.team5.safebridge.domain.chat.converter.ChatConverter;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataWithNewChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TranslatedDataDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.team.service.TeamQueryService;
import org.gdsccau.team5.safebridge.domain.term.dto.TermDto;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.term.service.TermQueryService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermCommandService;
import org.gdsccau.team5.safebridge.domain.translation.service.TranslationCommandService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatSendService {

    private static final String CHAT_SUB_URL = "/sub/chats/";
    private static final String TEAMS_SUB_URL = "/sub/teams/";
    private static final String TRANSLATE_SUB_URL = "/sub/translate/";

    private final TranslationCommandService translationCommandService;
    private final TermQueryService termQueryService;
    private final TranslatedTermCommandService translatedTermCommandService;
    private final TeamQueryService teamQueryService;
    private final UserTeamQueryService userTeamQueryService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TermManager termManager;
    private final RedisManager redisManager;
    private final Cache<String, Boolean> translatedTermCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();
    private final Cache<String, Boolean> translationCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();
    private final Cache<String, CompletableFuture<TranslatedDataDto>> translationAPICache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    public void sendChatMessage(final TermDataWithNewChatDto result, final String name, final Chat chat,
                                final Long teamId) {
        messagingTemplate.convertAndSend(CHAT_SUB_URL + teamId,
                ChatConverter.toChatResponseDto(name, chat, result.getTerms()));
    }

    public void sendTranslatedMessage(final TermDataWithNewChatDto result, final Language language, final Chat chat,
                                      final Long teamId, final Long userId) {
        String translationCacheKey = language + ":" + chat.getId();
        CompletableFuture<TranslatedDataDto> future = translationAPICache.get(translationCacheKey, key ->
                termManager.translate(result.getNewChat(), result.getTerms(), language));
        future.thenAccept(dto -> {
            dto.getTtSet().forEach(data -> createTranslatedTerm(language, data));
            createTranslation(language, dto, chat);
            messagingTemplate.convertAndSend(TRANSLATE_SUB_URL + teamId + "/" + userId,
                    ChatConverter.toTranslatedTextResponseDto(dto.getTranslatedText(), dto.getTranslatedTerms(),
                            chat.getId()));
        });
        // TODO 비동기 처리에 대한 예외처리
    }

    public void sendTeamData(final Chat chat, final Long teamId, final Long userId) {
        TeamListDto teamListDto = this.refreshRedisValue(chat, teamId, userId);
        messagingTemplate.convertAndSend(TEAMS_SUB_URL + userId, teamListDto);
    }

    private TeamListDto refreshRedisValue(final Chat chat, final Long teamId, final Long userId) {
        String inRoomKey = redisManager.getInRoomKey(userId, teamId);
        String unReadMessageKey = redisManager.getUnReadMessageKey(userId, teamId);
        String teamListKey = redisManager.getTeamListKey(userId);

        int inRoom = redisManager.getInRoomOrDefault(inRoomKey,
                () -> userTeamQueryService.findInRoomByUserIdAndTeamId(userId, teamId));
        if (inRoom == 0) {
            redisManager.updateUnReadMessage(unReadMessageKey);
            redisManager.updateUnReadMessageDirtySet(userId, teamId);
        }
        redisManager.updateTeamList(teamListKey, teamId, chat);

        return createTeamListDto(
                teamId, chat.getText(), chat.getCreatedAt(), redisManager.getUnReadMessageOrDefault(unReadMessageKey,
                        () -> userTeamQueryService.findUnReadMessageByUserIdAndTeamId(userId, teamId))
        );
    }

    private void createTranslatedTerm(final Language language, final TermDto.CreateTranslatedTermEntityDto data) {
        String isNewTermKey = language + ":" + data.getWord();
        if (translatedTermCache.asMap().putIfAbsent(isNewTermKey, true) == null) {
            Term term = termQueryService.findTermByWord(data.getWord());
            translatedTermCommandService.createTranslatedTerm(term, data.getLanguage(), data.getTranslatedWord());
        }
    }

    private void createTranslation(final Language language, final TranslatedDataDto dto, final Chat chat) {
        String isNewChatKey = language + ":" + chat.getId();
        if (translationCache.asMap().putIfAbsent(isNewChatKey, true) == null) {
            translationCommandService.createTranslation(dto.getTranslatedText(), language, chat.getId());
        }
    }

    private TeamListDto createTeamListDto(final Long teamId, final String lastChat,
                                          final LocalDateTime lastChatTime, final int unReadMessage) {
        return TeamListDto.builder()
                .teamId(teamId)
                .teamName(teamQueryService.findNameByTeamId(teamId))
                .lastChat(lastChat)
                .lastChatTime(lastChatTime)
                .unReadMessage(unReadMessage)
                .numberOfUsers(userTeamQueryService.countNumOfUsersByTeamId(teamId))
                .build();
    }
}
