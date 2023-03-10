package com.application.fusamate.rest.controller.sale;

import com.application.fusamate.dto.UpdateCustomerDto;
import com.application.fusamate.service.CustomerService;
import com.application.fusamate.service.sale.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/account")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class AccountController {

    private final OrderService orderService;
    private final CustomerService customerService;

    @GetMapping("/get-orders")
    public ResponseEntity<?> getOrdersByUserId(@RequestParam String email) {
        return ResponseEntity.ok().body(orderService.getSaleOrdersByEmail(email));
    }

    @GetMapping("/get-order-detail")
    public ResponseEntity<?> getOrderDetailById(@RequestParam Long id) {
        return ResponseEntity.ok().body(orderService.getOrdersById(id));
    }

    @GetMapping("/get-profile")
    public ResponseEntity<?> getUserProfileByUserId(@RequestParam Long userId) {
        return ResponseEntity.ok().body(customerService.getUserProfileByUserId(userId));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateCustomerDto customerDto,
                                               @RequestParam Long userId) throws Exception {
        return ResponseEntity.ok().body(customerService.updateProfileByUserId(customerDto, userId));
    }

}
