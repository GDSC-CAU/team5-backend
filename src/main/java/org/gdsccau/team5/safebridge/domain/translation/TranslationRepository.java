package org.gdsccau.team5.safebridge.domain.translation;


import org.gdsccau.team5.safebridge.domain.translation.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
}
