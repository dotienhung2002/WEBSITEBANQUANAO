package com.application.fusamate.service;

import com.application.fusamate.dto.CategoryDto;
import com.application.fusamate.dto.UpdateCategoryDto;
import com.application.fusamate.entity.Category;
import com.application.fusamate.model.CategorySearchCriteriaModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
  Category createCategory(CategoryDto categoryDto) throws Exception;
  Category getCategoryById(Integer Id);
  Category updateCategoryById(UpdateCategoryDto categoryDto, Integer Id) throws Exception;
  Page<Category> getCategories(CategorySearchCriteriaModel categorySearchCriteriaModel);
  List<Category> getCategoriesByStatus(int status);
}
