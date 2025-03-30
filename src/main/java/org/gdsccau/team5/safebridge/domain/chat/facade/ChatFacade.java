package org.gdsccau.team5.safebridge.domain.chat.facade;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.common.term.TermManager;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataWithNewChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto.ChatMessageRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.WorkResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatCommandService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatQueryService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatSendService;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.service.TeamQueryService;
import org.gdsccau.team5.safebridge.domain.term.service.TermMetaDataCommandService;
import org.gdsccau.team5.safebridge.domain.term.service.TermQueryService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermQueryService;
import org.gdsccau.team5.safebridge.domain.user.dto.UserDto.UserIdAndLanguageDto;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.gdsccau.team5.safebridge.domain.user.service.UserQueryService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamQueryService;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ChatFacade {

    private final ChatCommandService chatCommandService;
    private final ChatSendService chatSendService;
    private final ChatQueryService chatQueryService;
    private final TeamQueryService teamQueryService;
    private final UserQueryService userQueryService;
    private final UserTeamQueryService userTeamQueryService;
    private final TermQueryService termQueryService;
    private final TermMetaDataCommandService termMetaDataCommandService;
    private final TranslatedTermQueryService translatedTermQueryService;
    private final TermManager termManager;

    public void chat(final ChatMessageRequestDto chatRequestDto, final Long teamId, final Chat chat) {
        TermDataWithNewChatDto result = termManager.query(chatRequestDto.getMessage()); // 현장용어 추출
        chatSendService.sendChatMessage(result, chatRequestDto.getName(), chat, teamId); // 채팅은 즉시 전송

        // 채팅방에 속한 모든 사용자의 Id와 언어 가져오기
        List<UserIdAndLanguageDto> dtos = userTeamQueryService.findAllUserIdAndLanguageByTeamId(teamId);

        // 채팅방에 속한 모든 사용자에 대해 번역 데이터를 전송하고 채팅방 순서를 갱신한다.
        dtos.forEach(dto -> {
            Language language = dto.getLanguage();
            chatSendService.sendTranslatedMessage(result, language, chat, teamId, dto.getUserId());
            chatSendService.sendTeamData(chat, teamId, dto.getUserId());
        });

        // 현장용어를 위한 Local Cache 업데이트
        termMetaDataCommandService.updateTermMetaDataInLocalCache(result.getTerms(), getLanguageSet(dtos), chat.getCreatedAt());
    }

    @Transactional
    public Chat createChat(final ChatMessageRequestDto chatRequestDto, final Long teamId) {
        User user = userQueryService.findByUserId(chatRequestDto.getUserId());
        Team team = teamQueryService.findByTeamId(teamId);
        return chatCommandService.createChat(chatRequestDto, user, team);
    }

    public Map<String, Object> findAllChats(final String role, final Long cursorId, final Long userId,
                                            final Long teamId) {
        Language language = userQueryService.findLanguageByUserId(userId);
        Slice<ChatMessageWithIsReadResponseDto> chatSlice = chatQueryService.findAllChatsByTeamId(Role.valueOf(role),
                cursorId, teamId, language);
        LocalDateTime accessDate = userTeamQueryService.findAccessDateByUserIdAndTeamId(userId, teamId);
        for (ChatMessageWithIsReadResponseDto chatMessage : chatSlice.getContent()) {
            chatMessage.setRead(chatMessage.getSendTime().isBefore(accessDate));
            // 메시지에 포함된 {현장용어 : 번역용어} 쌍 담아서 보내기
            if (role.equals("MEMBER")) {
                Map<String, String> wordZip = new HashMap<>();
                List<String> words = termManager.query(chatMessage.getMessage()).getTerms().stream()
                        .map(TermDataDto::getTerm)
                        .toList();
                for (String word : words) {
                    Long termId = termQueryService.findTermIdByWord(word);
                    String translatedWord = translatedTermQueryService.findTranslatedWordByLanguageAndTermId(language,
                            termId);
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
        List<Long> teamIds = userTeamQueryService.findAllTeamIdByUserId(userId);
        return chatQueryService.findAllWorks(teamIds);
    }

    private Set<Language> getLanguageSet(final List<UserIdAndLanguageDto> dtos) {
        Set<Language> languageSet = new HashSet<>();
        dtos.forEach(dto -> languageSet.add(dto.getLanguage()));
        return languageSet;
    }
}
