package org.gdsccau.team5.safebridge.domain.translatedTerm.repository;

import java.util.Optional;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.translatedTerm.entity.TranslatedTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TranslatedTermRepository extends JpaRepository<TranslatedTerm, Long> {

    @Query("SELECT tt.word FROM TranslatedTerm tt WHERE tt.language = :language AND tt.term.id = :termId")
    Optional<String> findTranslatedTermByLanguageAndTermId(final Language language, final Long termId);
}
