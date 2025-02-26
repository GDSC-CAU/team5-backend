package org.gdsccau.team5.safebridge.domain.report.repository;

import java.util.List;
import org.gdsccau.team5.safebridge.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

  List<Report> findAllByLeaderId(Long leaderId);
}
