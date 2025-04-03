package org.gdsccau.team5.safebridge.domain.term.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.term.dto.TermDto.TermIdAndWordDto;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TermRepository extends JpaRepository<Term, Long> {

    @Modifying
    @Query(value = "INSERT INTO term (word, meaning) "
            + "VALUES (:word, :meaning) "
            + "ON DUPLICATE KEY UPDATE meaning = :meaning", nativeQuery = true)
    void upsertTerm(@Param("word") String word, @Param("meaning") String meaning);

    @Query("SELECT t FROM Term t WHERE t.word = :word ORDER BY t.id ASC LIMIT 1")
    Optional<Term> findTermByWord(@Param("word") final String word);

    @Query("SELECT t.id FROM Term t WHERE t.word = :word ORDER BY t.id ASC LIMIT 1")
    Optional<Long> findTermIdByWord(@Param("word") final String word);

    @Query("SELECT count(t) > 0 FROM Term t WHERE t.word = :word")
    boolean existsByWord(@Param("word") final String word);

    @Query("SELECT new org.gdsccau.team5.safebridge.domain.term.dto.TermDto$TermIdAndWordDto(t.id, t.word) "
            + "FROM Term t "
            + "WHERE t.word IN :words")
    List<TermIdAndWordDto> findTermIdAndWordByWords(@Param("words") List<String> words);
}
