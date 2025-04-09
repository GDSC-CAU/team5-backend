package org.gdsccau.team5.safebridge.domain.term.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.term.dto.TermDto.TermIdAndWordDto;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.term.repository.TermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TermQueryService {

    private final TermRepository termRepository;

    @Transactional(readOnly = true)
    public Term findTermByWord(final String word) {
        return termRepository.findTermByWord(word).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<TermIdAndWordDto> findTermIdAndWord(final List<String> words) {
        return termRepository.findTermIdAndWordByWords(words);
    }
}
