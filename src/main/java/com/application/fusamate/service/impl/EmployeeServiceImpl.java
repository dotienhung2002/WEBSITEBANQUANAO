package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.EmployeeDto;
import com.application.fusamate.dto.UpdateProfileDto;
import com.application.fusamate.entity.Employee;
import com.application.fusamate.model.EmployeeSearchCriteriaModel;
import com.application.fusamate.repository.EmployeeRepository;
import com.application.fusamate.repository.criteria.EmployeeCriteriaRepository;
import com.application.fusamate.service.EmployeeService;
import com.application.fusamate.utils.BaseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeCriteriaRepository employeeCriteriaRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsername(username);
        if (employee == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("user name not found");
        } else {
            log.info("User found in the database: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(new BaseHelper().convertRole(employee.getRole())));
        System.out.println(authorities);
        return new User(employee.getUsername(), employee.getPassword(), authorities);
    }

    @Override
    public Employee getEmployee(String username) {
        Employee employee = employeeRepository.findByUsername(username);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee does not exist!");
        }
        return employee;
    }

    @Override
    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee does not exist!"));
    }

    @Override
    public Employee createEmployee(EmployeeDto employeeDto) throws Exception {
        Employee employee = new Employee();
        if (employeeRepository.findByUsername(employeeDto.getUsername().trim()) != null) {
            throw new Exception(Constants.DUPLICATED_USER);
        } else if (employeeRepository.findByEmail(employeeDto.getEmail().trim()) != null) {
            throw new Exception(Constants.DUPLICATED_USER_EMAIL);
        } else if (employeeRepository.findByIdentityCard(employeeDto.getIdentityCard().trim()) != null) {
            throw new Exception(Constants.DUPLICATED_USER_IDENTITY_CARD);
        } else if (employeeRepository.findByPhone(employeeDto.getPhone().trim()) != null) {
            throw new Exception(Constants.DUPLICATED_USER_PHONE);
        }
        String password = employeeDto.getUsername() + Constants.SUFFIX_PASSWORD_DEFAULT;
        BeanUtils.copyProperties(employeeDto, employee);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        log.info("Created new employee: {}", employee);
        return employeeRepository.save(employee);
    }
    @Override
    public Employee updateEmployee(EmployeeDto employeeDto, Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee does not exist!"));
        BeanUtils.copyProperties(employeeDto,employee);
        employee.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        log.info("Updated employee: {}",employee);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateProfile(UpdateProfileDto employeeDto, Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee does not exist!"));
        BeanUtils.copyProperties(employeeDto,employee);
        log.info("Updated profile: {}",employee);
        return employeeRepository.save(employee);
    }

    @Override
    public Page<Employee> getEmployees(EmployeeSearchCriteriaModel employeeSearchCriteria) {
        return employeeCriteriaRepository.findAllWithFilters(employeeSearchCriteria);
    }
}
