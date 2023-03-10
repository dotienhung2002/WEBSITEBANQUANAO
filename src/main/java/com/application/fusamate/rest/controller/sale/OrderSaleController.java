package com.application.fusamate.rest.controller.sale;

import com.application.fusamate.dto.sale.OrdersSaleDto;
import com.application.fusamate.service.sale.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/order")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class OrderSaleController {

    private final OrderService orderService;

    @PostMapping("/payCart")
    public ResponseEntity<?> payCart(@RequestBody OrdersSaleDto ordersSaleDto) {
        return orderService.payCart(ordersSaleDto);
    }

    
    @PutMapping("/cancel-order")
    public ResponseEntity<?> cancelOrder(@RequestParam Long id) {
        return orderService.cancelOrder(id);
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<?> getOrderDetailById(@PathVariable Long id) {
        return ResponseEntity.ok().body(orderService.getOrdersById(id));
    }

}
