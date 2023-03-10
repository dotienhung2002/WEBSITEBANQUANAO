package com.application.fusamate.service;

import com.application.fusamate.dto.PromotionProductDto;
import com.application.fusamate.dto.UpdatePromotionProductDto;
import com.application.fusamate.entity.PromotionProduct;
import com.application.fusamate.model.PromotionProductSearchCriteriaModel;
import org.springframework.data.domain.Page;

public interface PromotionProductService {
    PromotionProduct getPromotionProduct(Long id);
    PromotionProduct createPromotionProduct(PromotionProductDto promotionProductDto) throws Exception;
    PromotionProduct updatePromotionProduct(UpdatePromotionProductDto updatePromotionProductDto, Long id) throws Exception;
    Page<PromotionProduct> getPromotionProducts(PromotionProductSearchCriteriaModel promotionProductSearchCriteriaModel);
}
