package com.application.fusamate.service;

import com.application.fusamate.dto.GetProductDto;
import com.application.fusamate.dto.ProductDto;
import com.application.fusamate.dto.UpdateProductDto;
import com.application.fusamate.entity.Product;
import com.application.fusamate.model.ProductSearchCriteriaModel;
import org.springframework.data.domain.Page;

public interface ProductService {

    Product createProduct(ProductDto productDto) throws Exception;

    GetProductDto getProductById(Long id);

    Product updateProduct(UpdateProductDto updateProductDto, Long id) throws Exception;

    Page<Product> getProducts (ProductSearchCriteriaModel productSearchCriteriaModel);

    Boolean updateAvailableById(Integer available, Long id);

}
