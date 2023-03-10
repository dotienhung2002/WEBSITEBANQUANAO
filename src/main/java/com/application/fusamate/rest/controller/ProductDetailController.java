package com.application.fusamate.rest.controller;

import com.application.fusamate.dto.ProductDetailDto;
import com.application.fusamate.dto.UpdateProductDetailDto;
import com.application.fusamate.entity.ProductDetail;
import com.application.fusamate.model.ProductDetailSearchCriteriaModel;
import com.application.fusamate.model.ResponseChangeDataModel;
import com.application.fusamate.model.ResponseGetDataModel;
import com.application.fusamate.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/product-detail")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class ProductDetailController {

    private final ProductDetailService productDetailService;

    @PostMapping("{product-id}/create")
    public ResponseEntity<?> createProductDetail(@PathVariable(name = "product-id") Long productId,
                                           @RequestBody @Valid ProductDetailDto productDetailDto) throws Exception {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(productDetailService.
                        createProductDetail(productId, productDetailDto), HttpStatus.OK.value()));
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<?> getProductDetailById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok().body(productDetailService.getProductDetailById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProductDetailById(@RequestBody @Valid UpdateProductDetailDto updateProductDetailDto,
                                               @PathVariable Long id) throws Exception {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(
                        productDetailService.updateProductDetailById(updateProductDetailDto, id), HttpStatus.OK.value())
        );
    }

    @PostMapping("{product-id}/get-list")
    public ResponseEntity<?> getProductDetailsByProductId(@PathVariable("product-id") Long productId,
                                                          @RequestBody ProductDetailSearchCriteriaModel
                                                                  productDetailSearchCriteriaModel) {
        Page<ProductDetail> productDetailPage = productDetailService
                .getProductDetailsByProductId(productId, productDetailSearchCriteriaModel);
        return ResponseEntity.ok().body(
                new ResponseGetDataModel(productDetailPage.getContent(), productDetailPage.getNumber(),
                productDetailPage.getTotalElements(), productDetailPage.getTotalPages()));
    }

}
