package com.application.fusamate.repository;

import com.application.fusamate.entity.Employee;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository  extends JpaRepository<Employee,Long> {
    Employee findByUsername(String username);
    Employee findByEmail(String email);
    Employee findByIdentityCard(String identityCard);
    Employee findByPhone(String username);
    Optional<Employee> findById (Long id);

    long countByStatus(@NonNull int status);

}
