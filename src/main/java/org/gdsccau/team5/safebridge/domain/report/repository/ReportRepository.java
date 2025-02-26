package org.gdsccau.team5.safebridge.domain.report.repository;

import org.gdsccau.team5.safebridge.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
