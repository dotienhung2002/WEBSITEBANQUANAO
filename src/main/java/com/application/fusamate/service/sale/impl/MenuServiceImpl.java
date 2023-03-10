package com.application.fusamate.service.sale.impl;

import com.application.fusamate.dto.sale.BrandSaleDto;
import com.application.fusamate.dto.sale.MadeInSaleDto;
import com.application.fusamate.dto.sale.ProductSetSaleDto;
import com.application.fusamate.entity.Brand;
import com.application.fusamate.entity.MadeIn;
import com.application.fusamate.entity.ProductSet;
import com.application.fusamate.repository.*;
import com.application.fusamate.service.sale.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {
    private final ProductSetRepository productSetRepository;
    private final BrandRepository brandRepository;
    private final MadeInRepository madeInRepository;
    @Override
    public List<ProductSetSaleDto> getActiveProductSetsAndCategory() {
        List<ProductSetSaleDto> getProductSetList = new ArrayList<>();
        List<ProductSet> productSets = productSetRepository.findAll();
        productSets.stream().filter(productSet -> productSet.getStatus() == 1)
                .forEach(productSet -> {
                    ProductSetSaleDto productSetSaleDto = new ProductSetSaleDto();
                    productSetSaleDto.setName(productSet.getName());
                    List<ProductSetSaleDto.CategorySaleDto> categorySaleDtoList = new ArrayList<>();
                    productSet.getListCategory().stream().filter(category -> category.getStatus() == 1)
                            .forEach(category -> categorySaleDtoList.add(
                                    new ProductSetSaleDto.CategorySaleDto(category.getId(), category.getName())));
                    productSetSaleDto.setCategoryList(categorySaleDtoList);
                    getProductSetList.add(productSetSaleDto);
                });
        return getProductSetList;
    }

    @Override
    public List<BrandSaleDto> getActiveBrands() {
        List<BrandSaleDto> getBrandList = new ArrayList<>();
        List<Brand> brands = brandRepository.findAll();
        brands.stream().filter(brand -> brand.getStatus() == 1)
                .forEach(brand -> getBrandList.add(new BrandSaleDto(brand.getId(), brand.getName())));
        return getBrandList;
    }

    @Override
    public List<MadeInSaleDto> getActiveMadeIns() {
        List<MadeInSaleDto> getMadeInList = new ArrayList<>();
        List<MadeIn> madeIns = madeInRepository.findAll();
        madeIns.forEach(madeIn -> getMadeInList.add(new MadeInSaleDto(madeIn.getId(), madeIn.getName())));
        return getMadeInList;
    }
}
