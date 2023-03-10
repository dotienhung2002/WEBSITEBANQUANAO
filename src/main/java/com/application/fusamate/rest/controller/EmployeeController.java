package com.application.fusamate.rest.controller;

import com.application.fusamate.configuration.MailTemplate;
import com.application.fusamate.dto.EmployeeDto;
import com.application.fusamate.dto.UpdateProfileDto;
import com.application.fusamate.entity.Employee;
import com.application.fusamate.model.EmployeeSearchCriteriaModel;
import com.application.fusamate.model.ResponseChangeDataModel;
import com.application.fusamate.model.ResponseGetDataModel;
import com.application.fusamate.service.EmployeeService;
import com.application.fusamate.utils.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
@RequiredArgsConstructor
@Validated
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmailService emailService;

    @PostMapping("/create")
    public ResponseEntity<?> createEmployee(@RequestBody @Valid EmployeeDto employeeDto) throws Exception {
        Employee entity = employeeService.createEmployee(employeeDto);
        if (entity != null) {
            emailService.sendMail(entity.getEmail().trim(), MailTemplate.AfterCreateAccount(entity.getName(), entity.getUsername(),entity.getNote()));
        }
        return ResponseEntity.ok().body(new ResponseChangeDataModel(entity, HttpStatus.OK.value()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable(value = "id") Long id, @RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.ok().body(new ResponseChangeDataModel( employeeService.updateEmployee(employeeDto, id),HttpStatus.OK.value()));
    }
    @PutMapping("/update-profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable(value = "id") Long id, @RequestBody UpdateProfileDto employee) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains("1")){
            return ResponseEntity.ok().body(new ResponseChangeDataModel( employeeService.updateProfile(employee, id),HttpStatus.OK.value()));
        }
        return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);
    }
    @GetMapping("/get-detail/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable(name = "id") @Positive Long id) {
        return ResponseEntity.ok().body(employeeService.getEmployee(id));
    }

    @PostMapping("/get-list")
    public ResponseEntity<?> getEmployees(@RequestBody EmployeeSearchCriteriaModel employeeSearchCriteria) {
        Page<Employee> pageElements = employeeService.getEmployees(employeeSearchCriteria);
        return ResponseEntity.ok().body(new ResponseGetDataModel(pageElements.getContent(),pageElements.getNumber(), pageElements.getTotalElements(), pageElements.getTotalPages()));
    }
}