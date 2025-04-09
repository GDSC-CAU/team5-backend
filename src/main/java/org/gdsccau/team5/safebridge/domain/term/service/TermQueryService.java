package org.gdsccau.team5.safebridge.domain.term.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.error.TermErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
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
        Term term = termRepository.findTermByWord(word).orElse(null);
        this.validate(term);
        return term;
    }

    @Transactional(readOnly = true)
    public List<TermIdAndWordDto> findTermIdAndWord(final List<String> words) {
        List<TermIdAndWordDto> dtos = termRepository.findTermIdAndWordByWords(words);
        this.validate(dtos);
        return dtos;
    }

    public <T> void validate(final T data) {
        if (data == null) {
            throw new ExceptionHandler(TermErrorCode.TERM_NOT_FOUND);
        }
    }

    public <T> void validate(final List<T> data) {
        if (data.isEmpty()) {
            throw new ExceptionHandler(TermErrorCode.TERM_NOT_FOUND);
        }
    }
}
