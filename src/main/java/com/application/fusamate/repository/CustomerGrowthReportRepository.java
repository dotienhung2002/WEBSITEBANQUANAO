package com.application.fusamate.repository;

import com.application.fusamate.entity.stats.CustomerGrowthReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerGrowthReportRepository extends JpaRepository<CustomerGrowthReport, Integer> {
}