package com.application.fusamate.service.sale;

import com.application.fusamate.dto.UpdateOrdersDto;
import com.application.fusamate.dto.sale.OrdersSaleDto;
import com.application.fusamate.entity.Orders;
import com.application.fusamate.model.OrderSearchCriteriaModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    Page<Orders> getOrders(OrderSearchCriteriaModel model);
    ResponseEntity<?> payCart(OrdersSaleDto ordersSaleDto);
    ResponseEntity<?> cancelOrder(Long id);
    Orders getOrdersById(Long id);
    Orders updateOrdersById(UpdateOrdersDto updateOrdersDto, Long id);
    Orders cancelOrdersById(UpdateOrdersDto updateOrdersDto, Long id);


    
    List<Orders> getSaleOrdersByEmail(String email);

}
