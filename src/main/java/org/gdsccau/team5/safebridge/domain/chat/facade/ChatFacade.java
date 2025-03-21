package org.gdsccau.team5.safebridge.domain.chat.facade;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.common.term.TermManager;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataWithNewChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto.ChatMessageRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.WorkResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatCheckService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatSendService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatService;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCheckService;
import org.gdsccau.team5.safebridge.domain.term.service.TermCheckService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermCheckService;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.gdsccau.team5.safebridge.domain.user.service.UserCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ChatFacade {

    private final ChatService chatService;
    private final ChatSendService chatSendService;
    private final ChatCheckService chatCheckService;
    private final TeamCheckService teamCheckService;
    private final UserCheckService userCheckService;
    private final UserTeamCheckService userTeamCheckService;
    private final TermCheckService termCheckService;
    private final TranslatedTermCheckService translatedTermCheckService;
    private final TermManager termManager;
    private final RedisManager redisManager;

    public void chat(final ChatMessageRequestDto chatRequestDto, final Long teamId, final Chat chat) {
        TermDataWithNewChatDto result = termManager.query(chatRequestDto.getMessage());
        chatSendService.sendChatMessage(result, chatRequestDto.getName(), chat, teamId);
        List<Long> userIds = userTeamCheckService.findAllUserIdByTeamId(teamId);
        for (Long userId : userIds) {
            Language language = userCheckService.findLanguageByUserId(userId);
            chatSendService.sendTranslatedMessage(result, language, chat, teamId, userId);
            chatSendService.sendTeamData(chat, teamId, userId);
        }
    }

    @Transactional
    public Chat createChat(final ChatMessageRequestDto chatRequestDto, final Long teamId) {
        User user = userCheckService.findByUserId(chatRequestDto.getUserId());
        Team team = teamCheckService.findByTeamId(teamId);
        return chatService.createChat(chatRequestDto, user, team);
    }

    public Map<String, Object> findAllChats(final String role, final Long cursorId, final Long userId,
                                            final Long teamId) {
        Language language = userCheckService.findLanguageByUserId(userId);
        Slice<ChatMessageWithIsReadResponseDto> chatSlice = chatCheckService.findAllChatsByTeamId(Role.valueOf(role),
                cursorId, teamId, language);
        LocalDateTime accessDate = userTeamCheckService.findAccessDateByUserIdAndTeamId(userId, teamId);
        for (ChatMessageWithIsReadResponseDto chatMessage : chatSlice.getContent()) {
            chatMessage.setRead(chatMessage.getSendTime().isBefore(accessDate));
            // 메시지에 포함된 {현장용어 : 번역용어} 쌍 담아서 보내기
            if (role.equals("MEMBER")) {
                Map<String, String> wordZip = new HashMap<>();
                List<String> words = termManager.query(chatMessage.getMessage()).getTerms().stream()
                        .map(TermDataDto::getTerm)
                        .toList();
                for (String word : words) {
                    Long termId = termCheckService.findTermIdByWord(word);
                    // 밑에 쿼리가 왜 여러 레코드가 리턴되는거지?
                    String translatedWord = translatedTermCheckService.findTranslatedTermByLanguageAndTermId(language,
                            termId);
//                    String translatedTermKey = redisManager.getTranslatedTermKey(termId, language);
//                    String translatedTerm = redisManager.getTranslatedTerm(translatedTermKey);
                    wordZip.put(word, translatedWord);
                }
                chatMessage.setTranslatedTerms(wordZip);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("messages", chatSlice.getContent());
        response.put("hasNext", chatSlice.hasNext());
        return response;
    }

    public List<WorkResponseDto> findAllWorks(final Long userId) {
        List<Long> teamIds = userTeamCheckService.findAllTeamIdByUserId(userId);
        return chatCheckService.findAllWorks(teamIds);
    }
}
