package org.gdsccau.team5.safebridge.domain.demo.controller;

import jakarta.transaction.Transactional;
import java.util.List;
import org.gdsccau.team5.safebridge.domain.demo.dto.DemoDto;
import org.gdsccau.team5.safebridge.domain.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Transactional
public class DemoController {

  private final DemoService demoService;

  @Autowired
  public DemoController(DemoService demoService) {
    this.demoService = demoService;
  }

  @GetMapping
  public List<DemoDto> getAllUsers() {
    return demoService.getAllDemo();
  }
}
