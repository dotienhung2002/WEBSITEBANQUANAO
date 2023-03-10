package com.application.fusamate.service;

import com.application.fusamate.dto.EmployeeDto;
import com.application.fusamate.dto.UpdateProfileDto;
import com.application.fusamate.entity.Employee;
import com.application.fusamate.model.EmployeeSearchCriteriaModel;
import org.springframework.data.domain.Page;

public interface EmployeeService {
    Employee getEmployee(String username);
    Employee getEmployee(Long id);
    Employee createEmployee(EmployeeDto employee) throws Exception;
    Employee updateEmployee(EmployeeDto employee, Long id);
    Employee updateProfile(UpdateProfileDto employee, Long id);
    Page<Employee> getEmployees(EmployeeSearchCriteriaModel employeeSearchCriteria);
}
