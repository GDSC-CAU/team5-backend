package org.gdsccau.team5.safebridge.domain.term.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.term.dto.TermDto.TermIdAndWordDto;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.term.repository.TermRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TermQueryService {

    private final TermRepository termRepository;

    public Term findTermByWord(final String word) {
        return termRepository.findTermByWord(word).orElse(null);
    }

    public Long findTermIdByWord(final String word) {
        return termRepository.findTermIdByWord(word).orElse(null);
    }

    public List<TermIdAndWordDto> findTermIdAndWord(final List<String> words) {
        return termRepository.findTermIdAndWordByWord(words);
    }
}
