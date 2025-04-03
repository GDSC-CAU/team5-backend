package org.gdsccau.team5.safebridge.domain.translation;


import java.util.Optional;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.translation.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TranslationRepository extends JpaRepository<Translation, Long> {

    @Query("SELECT count(t) > 0 FROM Translation t WHERE t.chat.id = :chatId AND t.language = :language")
    boolean existsByChatIdAndLanguage(@Param("chatId") final Long chatId, @Param("language") final Language language);

    @Query("SELECT t.text FROM Translation t WHERE t.chat.id = :chatId AND t.language = :language")
    Optional<String> findTranslatedTextByChatIdAndLanguage(@Param("chatId") final Long chatId,
                                                           @Param("language") final Language language);
}
