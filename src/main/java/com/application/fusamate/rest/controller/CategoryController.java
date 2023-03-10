package com.application.fusamate.rest.controller;


import com.application.fusamate.dto.CategoryDto;
import com.application.fusamate.dto.UpdateCategoryDto;
import com.application.fusamate.entity.Category;
import com.application.fusamate.model.CategorySearchCriteriaModel;
import com.application.fusamate.model.ResponseChangeDataModel;
import com.application.fusamate.model.ResponseGetDataModel;
import com.application.fusamate.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;


@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("/create")
    public ResponseEntity<?> insertCategory(@RequestBody @Valid CategoryDto categoryDto) throws Exception {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(categoryService.createCategory(categoryDto), HttpStatus.OK.value()));
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable @Positive Integer id) {
        return ResponseEntity.ok().body(categoryService.getCategoryById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategoryById(@PathVariable @Positive Integer id, @RequestBody UpdateCategoryDto categoryDto) throws Exception {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(categoryService.updateCategoryById(categoryDto, id), HttpStatus.OK.value())
        );
    }

    @PostMapping("/get-list")
    public ResponseEntity<?> getAllCategories(@RequestBody CategorySearchCriteriaModel categorySearchCriteriaModel) {
        Page<Category> categoryPage = categoryService.getCategories(categorySearchCriteriaModel);
        return ResponseEntity.ok().body(new ResponseGetDataModel(categoryPage.getContent(), categoryPage.getNumber(),
                categoryPage.getTotalElements(), categoryPage.getTotalPages()));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllActiveCategories() {
        return ResponseEntity.ok().body(categoryService.getCategoriesByStatus(1));
    }

}
