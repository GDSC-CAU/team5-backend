package org.gdsccau.team5.safebridge.domain.term.repository;

import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TermRepository extends JpaRepository<Term, Long> {

    @Query("SELECT t FROM Term t WHERE t.word = :word")
    Optional<Term> findTermByWord(final String word);

    @Query("SELECT t.id FROM Term t WHERE t.word = :word")
    Optional<Long> findTermIdByWord(final String word);
}
