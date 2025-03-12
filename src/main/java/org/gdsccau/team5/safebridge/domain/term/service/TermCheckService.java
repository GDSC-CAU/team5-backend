package org.gdsccau.team5.safebridge.domain.term.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.term.repository.TermRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TermCheckService {

    private final TermRepository termRepository;

    public Term findTermByWord(final String word) {
        return termRepository.findTermByWord(word).orElse(null);
    }

    public Long findTermIdByWord(final String word) {
        return termRepository.findTermIdByWord(word).orElse(null);
    }
}
