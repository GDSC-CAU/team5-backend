package org.gdsccau.team5.safebridge.domain.term.repository;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TermRepository extends JpaRepository<Term, Long> {

    @Query("SELECT t FROM Term t WHERE t.word = :word ORDER BY t.id ASC LIMIT 1")
    Optional<Term> findTermByWord(@Param("word") final String word);

    @Query("SELECT t.id FROM Term t WHERE t.word = :word ORDER BY t.id ASC LIMIT 1")
    Optional<Long> findTermIdByWord(@Param("word") String word);
}
