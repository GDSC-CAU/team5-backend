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

    public Long findTermIdByWord(final String word){
        Long termId = termRepository.findTermIdByWord(word).orElse(null);
        this.validate(termId);
        return termId;
    }

    public List<TermIdAndWordDto> findTermIdAndWord(final List<String> words) {
        return termRepository.findTermIdAndWordByWords(words);
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
