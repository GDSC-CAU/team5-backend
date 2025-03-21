package org.gdsccau.team5.safebridge.domain.chat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.common.term.TermManager;
import org.gdsccau.team5.safebridge.domain.chat.converter.ChatConverter;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataWithNewChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TranslatedDataDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCheckService;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.term.service.TermCheckService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermCheckService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermService;
import org.gdsccau.team5.safebridge.domain.translation.service.TranslationCheckService;
import org.gdsccau.team5.safebridge.domain.translation.service.TranslationService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatSendService {

    private static final String CHAT_SUB_URL = "/sub/chats/";
    private static final String TEAMS_SUB_URL = "/sub/teams/";
    private static final String TRANSLATE_SUB_URL = "/sub/translate/";

    private final TranslationService translationService;
    private final TranslationCheckService translationCheckService;
    private final TranslatedTermCheckService translatedTermCheckService;
    private final TranslatedTermService translatedTermService;
    private final TermCheckService termCheckService;
    private final TeamCheckService teamCheckService;
    private final UserTeamCheckService userTeamCheckService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TermManager termManager;
    private final RedisManager redisManager;

    public void sendChatMessage(final TermDataWithNewChatDto result, final String name, final Chat chat,
                                final Long teamId) {
        messagingTemplate.convertAndSend(CHAT_SUB_URL + teamId,
                ChatConverter.toChatResponseDto(name, chat, result.getTerms()));
    }

    public void sendTranslatedMessage(final TermDataWithNewChatDto result, final Language language, final Chat chat,
                                      final Long teamId, final Long userId) {
        // TODO 이미 번역되었다면 Local Cache -> DB 조회
        CompletableFuture<TranslatedDataDto> translatedText = termManager.translate(
                result.getNewChat(), result.getTerms(), language);
        translatedText.thenAccept(dto -> {
            translationService.createTranslation(dto.getTranslatedText(), language, chat.getId());
            messagingTemplate.convertAndSend(TRANSLATE_SUB_URL + teamId + "/" + userId,
                    ChatConverter.toTranslatedTextResponseDto(dto.getTranslatedText(), dto.getTranslatedTerms(),
                            chat.getId()));
        });
        // TODO 비동기 처리에 대한 예외처리
        // TODO Local Cache에 최근 호출 시각 + 누적 호출 횟수를 저장
    }

    public void sendTeamData(final Chat chat, final Long teamId, final Long userId) {
        TeamListDto teamListDto = this.refreshRedisValue(chat, teamId, userId);
        messagingTemplate.convertAndSend(TEAMS_SUB_URL + userId, teamListDto);
    }

    private TeamListDto refreshRedisValue(final Chat chat, final Long teamId, final Long userId) {
        String inRoomKey = redisManager.getInRoomKey(userId, teamId);
        String unReadMessageKey = redisManager.getUnReadMessageKey(userId, teamId);
        String zSetKey = redisManager.getZSetKey(userId);

        int inRoom = redisManager.getInRoomOrDefault(inRoomKey,
                () -> userTeamCheckService.findInRoomByUserIdAndTeamId(userId, teamId));
        if (inRoom == 0) {
            redisManager.updateUnReadMessage(unReadMessageKey);
            redisManager.updateUpdatedSet(userId, teamId);
        }
        redisManager.updateZSet(zSetKey, teamId, chat);

        return createTeamListDto(
                teamId, chat.getText(), chat.getCreatedAt(), redisManager.getUnReadMessage(unReadMessageKey,
                        () -> userTeamCheckService.findUnReadMessageByUserIdAndTeamId(userId, teamId))
        );
    }

    private TeamListDto createTeamListDto(final Long teamId, final String lastChat,
                                          final LocalDateTime lastChatTime, final int unReadMessage) {
        return TeamListDto.builder()
                .teamId(teamId)
                .teamName(teamCheckService.findNameByTeamId(teamId))
                .lastChat(lastChat)
                .lastChatTime(lastChatTime)
                .unReadMessage(unReadMessage)
                .numberOfUsers(userTeamCheckService.countNumOfUsersByTeamId(teamId))
                .build();
    }
}
