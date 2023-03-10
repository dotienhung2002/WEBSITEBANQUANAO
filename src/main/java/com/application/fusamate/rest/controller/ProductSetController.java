package com.application.fusamate.rest.controller;

import com.application.fusamate.dto.ProductSetDto;
import com.application.fusamate.entity.ProductSet;
import com.application.fusamate.model.ProductSetSearchCriteriaModel;
import com.application.fusamate.model.ResponseChangeDataModel;
import com.application.fusamate.model.ResponseGetDataModel;
import com.application.fusamate.service.ProductSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/product-set")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class ProductSetController {

    private final ProductSetService productSetService;

    @PostMapping("/get-list")
    public ResponseEntity<?> getAllProductSets(@RequestBody ProductSetSearchCriteriaModel productSetSearchCriteriaModel) {
        Page<ProductSet> productSetPage = productSetService.getAllProductSets(productSetSearchCriteriaModel);
        return ResponseEntity.ok().body(new ResponseGetDataModel(productSetPage.getContent(), productSetPage.getNumber(),
                productSetPage.getTotalElements(), productSetPage.getTotalPages()));
    }
    @GetMapping("/get-list")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(productSetService.getAll(),HttpStatus.OK.value())
        );
    }
    @GetMapping("/get-detail/{id}")
    public ResponseEntity<?> getProductSetById(@PathVariable @Positive Integer id) {
        return ResponseEntity.ok().body(productSetService.getProductSetById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProductSet(@RequestBody @Valid ProductSet productSet) throws Exception {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(productSetService.createProductSet(productSet), HttpStatus.OK.value()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProductSetById(@PathVariable Integer id, @RequestBody ProductSetDto productSet) throws Exception {
        return ResponseEntity.ok().body(new ResponseChangeDataModel(
                productSetService.updateProductSetId(productSet, id), HttpStatus.OK.value()));
    }

}
