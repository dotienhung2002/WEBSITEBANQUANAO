package com.application.fusamate.rest.controller.sale;

import com.application.fusamate.dto.sale.*;
import com.application.fusamate.model.ResponseChangeDataModel;
import com.application.fusamate.model.ResponseGetDataModel;
import com.application.fusamate.service.sale.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/cart")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class CartController {

    private final CartService cartService;

    @GetMapping("/getCart/{userAuthToken}")
    public ResponseEntity<?> getCart(@PathVariable String userAuthToken) {
        return ResponseEntity.ok().body(cartService.getCart(userAuthToken));
    }

    @PostMapping("/addItem")
    public ResponseEntity<?> addProductToCart(@RequestBody ItemAddedToCartDto itemAddedToCartDto) {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(cartService.addItemToCart(itemAddedToCartDto), HttpStatus.OK.value()));
    }

    @PostMapping("/updateItem")
    public ResponseEntity<?> updateItemQuantityToCart(@RequestBody ItemQuantityUpdateToCartDto itemQuantityUpdateToCartDto) {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(cartService.updateItemQuantityToCart(itemQuantityUpdateToCartDto), HttpStatus.OK.value())
        );
    }

    @DeleteMapping("/removeItem")
    public ResponseEntity<?> removeProductFromCart(@RequestBody ItemRemovedFromCartDto itemRemovedFromCartDto) {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(cartService.removeItemFromCart(itemRemovedFromCartDto), HttpStatus.OK.value()));
    }

    @DeleteMapping("/removeAll/{userAuthToken}")
    public ResponseEntity<?> removeAllFromCart(@PathVariable String userAuthToken) {
        cartService.removeAllFromCart(userAuthToken);
        return ResponseEntity.ok().build();
    }

}
