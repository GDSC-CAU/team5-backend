package org.gdsccau.team5.safebridge.domain.translation.repository;


import java.util.Optional;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.translation.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TranslationRepository extends JpaRepository<Translation, Long> {

}
