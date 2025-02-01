package org.gdsccau.team5.safebridge.domain.demo.repository;

import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.demo.entity.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoRepository extends JpaRepository<Demo, Long> {

  Optional<Demo> findByName(String name);

}
