package com.application.fusamate.rest.controller.sale;

import com.application.fusamate.service.sale.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/menu")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/get-classify-products")
    public ResponseEntity<?> getActiveProductSet() {
        return ResponseEntity.ok().body(menuService.getActiveProductSetsAndCategory());
    }

    @GetMapping("/get-brands")
    public ResponseEntity<?> getActiveBrands() {
        return ResponseEntity.ok().body(menuService.getActiveBrands());
    }

    @GetMapping("/get-madeIns")
    public ResponseEntity<?> getActiveMadeIns() {
        return ResponseEntity.ok().body(menuService.getActiveMadeIns());
    }

}
