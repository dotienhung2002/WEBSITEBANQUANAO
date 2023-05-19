package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.CustomerDto;
import com.application.fusamate.dto.UpdateCustomerDto;
import com.application.fusamate.entity.Customer;
import com.application.fusamate.model.CustomerSearchCriteriaModel;
import com.application.fusamate.repository.CustomerRepository;
import com.application.fusamate.repository.criteria.CustomerCriteriaRepository;
import com.application.fusamate.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerCriteriaRepository customerCriteriaRepository;

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Customer does not exist!"));
    }

    @Override
    public Customer createCustomer(CustomerDto customerDto) throws Exception {
        Customer customer = new Customer();
        if (customerRepository.findByEmail(customerDto.getEmail()) != null) {
            throw new Exception(Constants.DUPLICATED_USER_EMAIL);
        }
        BeanUtils.copyProperties(customerDto, customer);

        System.out.println(customer);

        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(UpdateCustomerDto customerDto, Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer does not exist!"));
        BeanUtils.copyProperties(customerDto, customer);
        customer.setUpdatedAt(new Date());
        customer.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(customer);
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Page<Customer> getCustomers(CustomerSearchCriteriaModel customerSearchCriteriaModel) {
        return customerCriteriaRepository.findAllWithFilters(customerSearchCriteriaModel);
    }

    @Override
    public Customer getUserProfileByUserId(Long userId) {
        return customerRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    @Override
    public Customer updateProfileByUserId(UpdateCustomerDto customerDto, Long userId) throws Exception {
        Customer updateCustomer = customerRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        // TODO: Need to fix
        if (customerRepository.findByPhone(customerDto.getPhone().trim()) != null
                && !customerDto.getPhone().trim().equals(updateCustomer.getPhone().trim()))
            throw new Exception(Constants.DUPLICATED_USER_PHONE);

        updateCustomer.setName(customerDto.getName().trim());
        updateCustomer.setPhone(customerDto.getPhone().trim());
        updateCustomer.setGender(customerDto.getGender());
        // updateCustomer.setBirthDay(customerDto.getBirthDay());
        // updateCustomer.setHeight(customerDto.getHeight());
        // updateCustomer.setWeight(customerDto.getWeight());
        updateCustomer.setUpdatedAt(new Date());

        return customerRepository.save(updateCustomer);
    }

    @Override
    public Customer getByEmailCustomer(String email) {
        return customerRepository.findByEmail(email);
    }

}