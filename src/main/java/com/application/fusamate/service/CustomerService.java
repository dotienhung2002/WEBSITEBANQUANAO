package com.application.fusamate.service;


import com.application.fusamate.dto.CustomerDto;
import com.application.fusamate.dto.UpdateCustomerDto;
import com.application.fusamate.entity.Customer;
import com.application.fusamate.model.CustomerSearchCriteriaModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {
    Customer getCustomer(Long id);
    Customer getByEmailCustomer(String getEmail);
    Customer createCustomer( CustomerDto customer) throws Exception;
    Customer updateCustomer(UpdateCustomerDto customer, Long id);
    List<Customer> getAllCustomers();
    Page<Customer> getCustomers(CustomerSearchCriteriaModel customerSearchCriteriaModel);
    Customer getUserProfileByUserId(Long userId);
    Customer updateProfileByUserId(UpdateCustomerDto customerDto, Long userId) throws Exception;
}


