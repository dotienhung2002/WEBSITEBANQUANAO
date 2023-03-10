package com.application.fusamate.service.impl;


import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.CategoryDto;
import com.application.fusamate.dto.UpdateCategoryDto;
import com.application.fusamate.entity.Category;
import com.application.fusamate.model.CategorySearchCriteriaModel;
import com.application.fusamate.repository.CategoryRepository;
import com.application.fusamate.repository.ProductSetRepository;
import com.application.fusamate.repository.criteria.CategoryCriteriaRepository;
import com.application.fusamate.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryCriteriaRepository categoryCriteriaRepository;
    private final ProductSetRepository productSetRepository;

    @Override
    public Category createCategory(CategoryDto categoryDto) throws Exception {
        log.info("Created new category : {}", categoryDto);
        if (productSetRepository.findById(categoryDto.getProductSetId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tập sản phẩm không tồn tại");
        }
        if (!categoryRepository.findByProductSet_IdAndNameLike(categoryDto.getProductSetId(), categoryDto.getName().trim()).isEmpty()) {
            throw new Exception(Constants.DUPLICATED_CATEGORY);
        }
        Category newCategory = new Category();
        BeanUtils.copyProperties(categoryDto, newCategory);
        newCategory.setName(newCategory.getName().trim());
        newCategory.setStatus(1);
        newCategory.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        newCategory.setProductSet(productSetRepository.findById(categoryDto.getProductSetId()).get());
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(Integer Id) {
        return categoryRepository.findById(Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loại sản phẩm không tồn tại"));
    }

    @Override
    public Category updateCategoryById(UpdateCategoryDto categoryDto, Integer Id) throws Exception {
        Category category = categoryRepository.findById(Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loại sản phẩm không tồn tại"));

        if (!categoryRepository.findByProductSet_IdAndNameLike(category.getProductSet().getId(), categoryDto.getName()).isEmpty()) {
            if (!categoryDto.getName().trim().equalsIgnoreCase(category.getName().trim()))
                throw new Exception(Constants.DUPLICATED_CATEGORY);
        }

        BeanUtils.copyProperties(categoryDto, category);

        category.setName(category.getName().trim());
        category.setUpdatedAt(new Date());
        category.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return categoryRepository.save(category);
    }

    @Override
    public Page<Category> getCategories(CategorySearchCriteriaModel categorySearchCriteriaModel) {
        return categoryCriteriaRepository.findAllWithFilters(categorySearchCriteriaModel);
    }

    @Override
    public List<Category> getCategoriesByStatus(int status) {
        return categoryRepository.findByStatus(status);
    }
}
