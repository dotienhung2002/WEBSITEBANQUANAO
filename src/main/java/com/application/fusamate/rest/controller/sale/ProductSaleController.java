package com.application.fusamate.rest.controller.sale;

import com.application.fusamate.service.ProductService;
import com.application.fusamate.service.sale.ProductSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/product")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class ProductSaleController {

    private final ProductSaleService productSaleService;
    private final ProductService productService;

    @GetMapping("/find")
    public ResponseEntity<?> findProducts(@RequestParam String keyword) {
        Map<Object, Object> returnResult = new HashMap();
        returnResult.put("data", productSaleService.findProducts(keyword));
        return ResponseEntity.ok().body(returnResult);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllProducts() {
        Map<Object, Object> returnResult = new HashMap();
        returnResult.put("data", productSaleService.getAllProducts());
        return ResponseEntity.ok().body(returnResult);
    }

    @GetMapping("/get-all-ready")
    public ResponseEntity<?> getAllReadyProducts() {
        Map<Object, Object> returnResult = new HashMap();
        returnResult.put("data", productSaleService.getAllActiveAndAvailableProducts());
        return ResponseEntity.ok().body(returnResult);
    }

    @GetMapping("/brand/get-all-ready/{brandId}")
    public ResponseEntity<?> getAllReadyProductsByBrand(@PathVariable Integer brandId) {
        Map<Object, Object> returnResult = new HashMap();
        returnResult.put("data", productSaleService.getAllActiveAndAvailableProductsByBrand(brandId));
        return ResponseEntity.ok().body(returnResult);
    }

    @GetMapping("/category/get-all-ready/{cateId}")
    public ResponseEntity<?> getAllReadyProductsByCategory(@PathVariable Integer cateId) {
        Map<Object, Object> returnResult = new HashMap();
        returnResult.put("data", productSaleService.getAllActiveAndAvailableProductsByCategory(cateId));
        return ResponseEntity.ok().body(returnResult);
    }
    @GetMapping("/get-detail/{id}")
    public ResponseEntity<?> getProductById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok().body(productService.getProductById(id));
    }


    @GetMapping("/madeIn/get-all-ready/{madeInId}")
    public ResponseEntity<?> getAllReadyProductsByMadeIn(@PathVariable Integer madeInId) {
        Map<Object, Object> returnResult = new HashMap();
        returnResult.put("data", productSaleService.getAllActiveAndAvailableProductsByMadeIn(madeInId));
        return ResponseEntity.ok().body(returnResult);
    }

}
