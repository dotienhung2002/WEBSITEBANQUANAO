package com.application.fusamate.service;

import com.application.fusamate.dto.BrandDto;
import com.application.fusamate.dto.UpdateBrandDto;
import com.application.fusamate.entity.Brand;
import com.application.fusamate.model.BrandSearchCriteriaModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {

    Brand createBrand(BrandDto brand) throws Exception;

    Brand getBrandById(Integer id);

    Brand updateBrandById(UpdateBrandDto brandDto, Integer id) throws Exception;

    Page<Brand> getBrands(BrandSearchCriteriaModel brandSearchCriteriaModel);

    List<Brand> getBrandsByStatus(int status);
}
