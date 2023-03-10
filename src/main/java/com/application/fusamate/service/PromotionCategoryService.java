package com.application.fusamate.service;

import com.application.fusamate.dto.PromotionCategoryDto;
import com.application.fusamate.dto.UpdatePromotionCategoryDto;
import com.application.fusamate.entity.PromotionCategory;
import com.application.fusamate.model.PromotionCategorySearchCriteriaModel;
import org.springframework.data.domain.Page;

public interface PromotionCategoryService {
    PromotionCategory getPromotionCategory(Integer id);

    PromotionCategory createPromotionCategory(PromotionCategoryDto promotionCategoryDto) throws Exception;

    PromotionCategory updatePromotionCategory(UpdatePromotionCategoryDto updatePromotionCategoryDto, Integer id) throws Exception;

    Page<PromotionCategory> getPromotionCategories(PromotionCategorySearchCriteriaModel promotionCategorySearchCriteriaModel);
}
