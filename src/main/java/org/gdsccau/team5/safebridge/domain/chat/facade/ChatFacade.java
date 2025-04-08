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
import org.gdsccau.team5.safebridge.domain.term.dto.TermDto.TermIdAndWordDto;
import org.gdsccau.team5.safebridge.domain.term.service.TermCommandService;
import org.gdsccau.team5.safebridge.domain.term.service.TermMetaDataCommandService;
import org.gdsccau.team5.safebridge.domain.term.service.TermQueryService;
import org.gdsccau.team5.safebridge.domain.translatedTerm.service.TranslatedTermQueryService;
import org.gdsccau.team5.safebridge.domain.user.dto.UserDto.UserIdAndLanguageDto;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.gdsccau.team5.safebridge.domain.user.service.UserQueryService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
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
    private final TermCommandService termCommandService;
    private final TermMetaDataCommandService termMetaDataCommandService;
    private final TranslatedTermQueryService translatedTermQueryService;
    private final TermManager termManager;

    public void chat(final ChatMessageRequestDto chatRequestDto, final Long teamId, final Chat chat) {
        TermDataWithNewChatDto result = termManager.query(chatRequestDto.getMessage()); // 현장용어 추출
        chatSendService.sendChatMessage(result, chatRequestDto.getName(), chat, teamId); // 채팅은 즉시 전송

        // 현장용어 추출 후 Term 엔티티 저장하기
        createTerms(result);

        // 채팅방에 속한 모든 사용자의 Id와 언어 가져오기
        List<UserIdAndLanguageDto> dtos = userTeamQueryService.findAllUserIdAndLanguageByTeamId(teamId);

        // 채팅방에 속한 모든 사용자에 대해 번역 데이터를 전송하고 채팅방 순서를 갱신한다.
        dtos.forEach(dto -> {
            Language language = dto.getLanguage();
            chatSendService.sendTranslatedMessage(result, language, chat.getId(), teamId, dto.getUserId());
            chatSendService.sendTeamData(chat, teamId, dto.getUserId());
        });

        // 현장용어를 위한 Local Cache 업데이트
        termMetaDataCommandService.updateTermMetaDataInLocalCache(result.getTerms(), getLanguageSet(dtos));
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
        chatSlice.getContent().forEach(chatMessage -> {
            // 이미 읽은 채팅인지 검사한다.
            chatMessage.setRead(chatMessage.getSendTime().isBefore(accessDate));
            // 관리자가 아닌 근로자라면, 채팅에 포함된 {현장용어 : 번역용어} 데이터가 필요하다.
            if (role.equals("MEMBER")) {
                setWordZipInChatMessage(chatMessage, language);
            }
        });
        Map<String, Object> response = new HashMap<>();
        response.put("messages", chatSlice.getContent());
        response.put("hasNext", chatSlice.hasNext());
        return response;
    }

    public List<WorkResponseDto> findAllWorks(final Long userId) {
        List<Long> teamIds = userTeamQueryService.findAllTeamIdByUserId(userId);
        return chatQueryService.findAllWorks(teamIds);
    }

    private void createTerms(final TermDataWithNewChatDto result) {
        result.getTerms()
                .forEach(dto -> {
                    String word = dto.getTerm();
                    String meaning = dto.getMeaning();
                    termCommandService.createTerm(word, meaning);
                });
    }

    private Set<Language> getLanguageSet(final List<UserIdAndLanguageDto> dtos) {
        Set<Language> languageSet = new HashSet<>();
        dtos.forEach(dto -> languageSet.add(dto.getLanguage()));
        return languageSet;
    }

    private void setWordZipInChatMessage(ChatMessageWithIsReadResponseDto chatMessage, final Language language) {
        Map<String, String> wordZip = getWordZipInChatMessage(chatMessage, language);
        chatMessage.setTranslatedTerms(wordZip);
    }

    private Map<String, String> getWordZipInChatMessage(final ChatMessageWithIsReadResponseDto chatMessage,
                                                        final Language language) {
        Map<String, String> wordZip = new HashMap<>();
        List<String> words = termManager.query(chatMessage.getMessage()).getTerms().stream()
                .map(TermDataDto::getTerm)
                .toList();

        List<TermIdAndWordDto> termIdAndWordDtos = termQueryService.findTermIdAndWord(words);
        Map<Long, String> termIdAndWordMap = getTermIdAndWordMap(termIdAndWordDtos);
        List<Long> termIds = getTermIds(termIdAndWordDtos);

        translatedTermQueryService.findTranslatedWordsByLanguageAndTermIds(language, termIds)
                .forEach(dto -> {
                    String translatedWord = dto.getTranslatedWord();
                    Long termId = dto.getTermId();
                    String word = termIdAndWordMap.get(termId);
                    wordZip.put(word, translatedWord);
                });
        return wordZip;
    }

    private Map<Long, String> getTermIdAndWordMap(final List<TermIdAndWordDto> termIdAndWordDtos) {
        Map<Long, String> termIdAndWordMap = new HashMap<>();
        termIdAndWordDtos.forEach(termIdAndWordDto -> {
            Long termId = termIdAndWordDto.getTermId();
            String word = termIdAndWordDto.getWord();
            termIdAndWordMap.put(termId, word);
        });
        return termIdAndWordMap;
    }

    private List<Long> getTermIds(final List<TermIdAndWordDto> termIdAndWordDtos) {
        return termIdAndWordDtos.stream()
                .map(TermIdAndWordDto::getTermId)
                .toList();
    }
}
