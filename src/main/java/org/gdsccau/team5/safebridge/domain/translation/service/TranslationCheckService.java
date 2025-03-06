package org.gdsccau.team5.safebridge.domain.translation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.translation.TranslationRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TranslationCheckService {

    private final TranslationRepository translationRepository;

}
