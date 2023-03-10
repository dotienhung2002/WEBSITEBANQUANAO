package com.application.fusamate.repository;

import com.application.fusamate.entity.Customer;
import com.application.fusamate.entity.stats.CustomerGrowthReport;

import lombok.NonNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    long countByStatus(@NonNull int status);

    Optional<Customer> findById(Long id);

    Customer findByEmail(String email);

    Optional<Customer> findByEmailLikeIgnoreCase(@NonNull String email);

    Customer findByPhone(String phone);

    @Query("select new CustomerGrowthReport(year(c.createdAt), month(c.createdAt), count(distinct c.email)) " +
            "from Customer c " +
            "group by 1,2")
    List<CustomerGrowthReport> getCustomerGrowthReport();

}
