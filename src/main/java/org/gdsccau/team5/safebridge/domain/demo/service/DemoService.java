package org.gdsccau.team5.safebridge.domain.demo.service;

import java.util.List;
import java.util.stream.Collectors;
import org.gdsccau.team5.safebridge.domain.demo.dto.DemoDto;
import org.gdsccau.team5.safebridge.domain.demo.entity.Demo;
import org.gdsccau.team5.safebridge.domain.demo.exception.DemoNotFoundException;
import org.gdsccau.team5.safebridge.domain.demo.repository.DemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

  private final DemoRepository demoRepository;

  @Autowired
  public DemoService(DemoRepository demoRepository) {
    this.demoRepository = demoRepository;
  }

  public List<DemoDto> getAllDemo() {
    return demoRepository.findAll().stream()
        .map(DemoDto::fromEntity)
        .collect(Collectors.toList());
  }

  public DemoDto getUserById(final Long id) {
    Demo demo = demoRepository.findById(id)
        .orElseThrow(() -> new DemoNotFoundException("존재하지 않는 Demo입니다."));
    return DemoDto.fromEntity(demo);
  }
}
