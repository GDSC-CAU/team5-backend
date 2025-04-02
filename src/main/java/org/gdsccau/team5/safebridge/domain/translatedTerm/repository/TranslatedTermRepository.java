package org.gdsccau.team5.safebridge.domain.translatedTerm.repository;

import java.util.List;
import java.util.Optional;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.translatedTerm.dto.TranslatedTermDto.TranslatedWordAndTermIdDto;
import org.gdsccau.team5.safebridge.domain.translatedTerm.entity.TranslatedTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TranslatedTermRepository extends JpaRepository<TranslatedTerm, Long> {

    @Modifying
    @Query(value = "INSERT INTO translated_term (language, word, term_id) "
            + "VALUES (:language, :word, :termId) "
            + "ON DUPLICATE KEY UPDATE word = VALUES(word)", nativeQuery = true)
    void upsertTranslatedTerm(@Param("language") Language language,
                              @Param("word") String word,
                              @Param("termId") Long termId);

    @Query("SELECT tt.word FROM TranslatedTerm tt WHERE tt.language = :language AND tt.term.id = :termId")
    Optional<String> findTranslatedWordByLanguageAndTermId(@Param("language") final Language language, @Param("termId") final Long termId);

    @Query("SELECT count(tt) > 0 FROM TranslatedTerm tt WHERE tt.language = :language AND tt.term.id = :termId")
    Boolean existsByLanguageAndTermId(@Param("language") final Language language, @Param("termId") final Long termId);

    @Query("SELECT new org.gdsccau.team5.safebridge.domain.translatedTerm.dto.TranslatedTermDto$TranslatedWordAndTermIdDto (tt.word, tt.term.id) "
            + "FROM TranslatedTerm tt "
            + "WHERE tt.language = :language AND tt.term.id IN :termIds")
    List<TranslatedWordAndTermIdDto> findTranslatedTermsByLanguageAndTermIds(@Param("language") final Language language, @Param("termIds") final List<Long> termIds);
}
