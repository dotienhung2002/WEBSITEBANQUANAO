package com.application.fusamate.rest.controller;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.configuration.MailTemplate;
import com.application.fusamate.dto.EmployeeDto;
import com.application.fusamate.entity.Employee;
import com.application.fusamate.model.ChangePasswordModel;
import com.application.fusamate.model.JwtRequestModel;
import com.application.fusamate.model.JwtResponseModel;
import com.application.fusamate.model.ResetPasswordModel;
import com.application.fusamate.repository.EmployeeRepository;
import com.application.fusamate.service.EmployeeService;
import com.application.fusamate.service.impl.EmployeeServiceImpl;
import com.application.fusamate.utils.EmailService;
import com.application.fusamate.utils.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final EmployeeServiceImpl userDetailsService;
    private final EmployeeService employeeService;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody @Valid JwtRequestModel request) throws Exception {
        System.out.println("login");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        Employee employee = employeeService.getEmployee(request.getUsername());
        if (employee.getStatus() == 0) {
            throw new Exception(Constants.USER_LOCKED);
        }
        EmployeeDto employeeDto = new EmployeeDto();
        BeanUtils.copyProperties(employee, employeeDto);
        final String accessToken = tokenManager.generateJwtToken(userDetails, 10 * 60 * 60);
        final String refreshToken = tokenManager.generateJwtToken(userDetails, 30 * 60 * 60);
        return ResponseEntity.ok(new JwtResponseModel(accessToken, refreshToken, employeeDto, HttpStatus.OK.value()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> ChangePassword(@RequestBody ChangePasswordModel request) throws Exception {
        Map response = new HashMap<>();
        Employee employee;
        String password;
        if (request.getVerifyToken() != null) {
            String username = tokenManager.getUsernameFromToken(request.getVerifyToken());
            employee = employeeService.getEmployee(username);
            if (employee == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee does not exist!");
            if (passwordEncoder.matches(request.getNew_password(),employee.getPassword())) {
                throw new Exception(Constants.SAME_OLD_PASSWORD);
            }
            employee.setPassword(passwordEncoder.encode(request.getNew_password().trim()));
            employeeRepository.save(employee);

            response.put("status", HttpStatus.OK.value());
        } else {
            employee = employeeService.getEmployee(request.getUsername());
            if (employee == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee does not exist!");
             password = request.getCurrent_password().trim();
            if (passwordEncoder.matches(password, employee.getPassword())) {
                if (passwordEncoder.matches(request.getNew_password().trim(),employee.getPassword())) {
                    throw new Exception(Constants.SAME_OLD_PASSWORD);
                }
                employee.setPassword(passwordEncoder.encode(request.getNew_password().trim()));
                employeeRepository.save(employee);
                response.put("status", HttpStatus.OK.value());

            } else {
                throw new Exception(Constants.WRONG_PASSWORD);
            }
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordModel request) throws Exception {
        Map response = new HashMap<>();
        String email = request.getEmail().trim();
        boolean checkEmail = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(email).matches();
        if (checkEmail) {
            Employee employee = employeeRepository.findByEmail(email);
            if (employee.getStatus() == 0) {
                throw new Exception(Constants.USER_LOCKED);
            }
            if (employee != null) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(employee.getUsername());
                String verifyToken = tokenManager.generateJwtToken(userDetails, 10 * 60 * 60);
                String link = "http://" + Constants.BASE_URL_FRONTEND + "/cms/reset-password/" + verifyToken;
                emailService.sendMail(request.getEmail().trim(), MailTemplate.verifyResetPassword(employee.getName(), link));
                response.put("status", HttpStatus.OK.value());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email does not exist!");
            }
        } else {
            throw new Exception(Constants.INVALID_EMAIL);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws Exception {
        String authorizationHeader = request.getHeader((HttpHeaders.AUTHORIZATION));
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String refresh_token = authorizationHeader.substring("Bearer".length());
            String username = tokenManager.getUsernameFromToken(refresh_token);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            final String accessToken = tokenManager.generateJwtToken(userDetails, 10 * 60 * 60);
            final String refreshToken = tokenManager.generateJwtToken(userDetails, 30 * 60 * 60);
            Map<String, String> response = new HashMap<>();
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            return ResponseEntity.ok().body(response);
        } else {
            throw new Exception("Refresh token missing!");
        }
    }

}
