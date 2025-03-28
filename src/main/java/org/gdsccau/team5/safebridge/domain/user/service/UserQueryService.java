package org.gdsccau.team5.safebridge.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public User findByUserId(final Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public Language findLanguageByUserId(final Long userId) {
        return userRepository.findLanguageByUserId(userId).orElse(Language.KOREAN);
    }
}
