package com.application.fusamate.service.sale;

import com.application.fusamate.dto.sale.FindProductDto;
import com.application.fusamate.dto.sale.GetAllProductSaleDto;

import java.util.List;

public interface ProductSaleService {

    List<FindProductDto> findProducts(String keyword);

    List<GetAllProductSaleDto> getAllProducts();

    List<GetAllProductSaleDto> getAllActiveAndAvailableProducts();

    List<GetAllProductSaleDto> getAllActiveAndAvailableProductsByBrand(Integer brandId);

    List<GetAllProductSaleDto> getAllActiveAndAvailableProductsByCategory(Integer cateId);

    List<GetAllProductSaleDto> getAllActiveAndAvailableProductsByMadeIn(Integer madeInId);

}
