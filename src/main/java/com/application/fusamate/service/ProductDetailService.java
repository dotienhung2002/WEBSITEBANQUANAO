package com.application.fusamate.service;

import com.application.fusamate.dto.ProductDetailDto;
import com.application.fusamate.dto.UpdateProductDetailDto;
import com.application.fusamate.entity.ProductDetail;
import com.application.fusamate.model.ProductDetailSearchCriteriaModel;
import org.springframework.data.domain.Page;

public interface ProductDetailService {

    ProductDetail createProductDetail(Long productId, ProductDetailDto productDetailDto) throws Exception;

    ProductDetail getProductDetailById(Long id);

    ProductDetail updateProductDetailById(UpdateProductDetailDto updateProductDetailDto, Long id) throws Exception;

    Page<ProductDetail> getProductDetailsByProductId(Long productId, ProductDetailSearchCriteriaModel productDetailSearchCriteriaModel);

}
