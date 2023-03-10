package com.application.fusamate.service.sale;

import com.application.fusamate.dto.sale.BrandSaleDto;
import com.application.fusamate.dto.sale.MadeInSaleDto;
import com.application.fusamate.dto.sale.ProductSetSaleDto;

import java.util.List;

public interface MenuService {

    List<ProductSetSaleDto> getActiveProductSetsAndCategory();

    List<BrandSaleDto> getActiveBrands();

    List<MadeInSaleDto> getActiveMadeIns();

}
