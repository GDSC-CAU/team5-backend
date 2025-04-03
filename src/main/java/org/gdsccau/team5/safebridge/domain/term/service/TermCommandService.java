package org.gdsccau.team5.safebridge.domain.term.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.term.repository.TermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TermCommandService {

    private final TermRepository termRepository;

    @Transactional
    public void createTerm(final String word, final String meaning) {
//        Term term = Term.builder()
//                .word(word)
//                .meaning(meaning)
//                .build();
//        return termRepository.save(term);
        termRepository.upsertTerm(word, meaning);
    }
}
