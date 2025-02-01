package org.gdsccau.team5.safebridge.domain.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import org.gdsccau.team5.safebridge.domain.demo.dto.DemoDto;
import org.gdsccau.team5.safebridge.domain.demo.entity.Demo;
import org.gdsccau.team5.safebridge.domain.demo.repository.DemoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
@Rollback
public class DemoServiceIntegrationTest {

  @Autowired
  private DemoService demoService;

  @Autowired
  private DemoRepository demoRepository;

  @Test
  @DisplayName("데모 모델 추가 및 조회")
  void testCreateDemoAndFindUser() {
    Demo demo = new Demo("John Doe");
    demoRepository.save(demo);

    // when
    DemoDto demoDto = demoService.getUserById(demo.getId());

    // then
    assertThat(demoDto).isNotNull();
    assertThat(demoDto.name()).isEqualTo("John Doe");
  }
}
