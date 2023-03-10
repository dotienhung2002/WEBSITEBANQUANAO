package com.application.fusamate.service;

import com.application.fusamate.dto.ProductSetDto;
import com.application.fusamate.entity.ProductSet;
import com.application.fusamate.model.ProductSetSearchCriteriaModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductSetService {
    ProductSet getProductSetById(Integer id);
    ProductSet createProductSet(ProductSet productSet) throws Exception;
    ProductSet updateProductSetId(ProductSetDto productSetDto, Integer id) throws Exception;
    Page<ProductSet> getAllProductSets(ProductSetSearchCriteriaModel productSetSearchCriteriaModel);
    List<ProductSet> getAll();
}
