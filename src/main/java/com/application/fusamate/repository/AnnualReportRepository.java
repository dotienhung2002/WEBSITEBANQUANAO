package com.application.fusamate.repository;

import com.application.fusamate.entity.stats.AnnualReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnualReportRepository extends JpaRepository<AnnualReport, Integer> {
}