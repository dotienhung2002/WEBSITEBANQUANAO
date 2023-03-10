package com.application.fusamate.repository;

import com.application.fusamate.entity.stats.TopSalesCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopSalesCustomerRepository extends JpaRepository<TopSalesCustomer, Long> {
}