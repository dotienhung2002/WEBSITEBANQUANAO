package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.BrandDto;
import com.application.fusamate.dto.UpdateBrandDto;
import com.application.fusamate.entity.Brand;
import com.application.fusamate.model.BrandSearchCriteriaModel;
import com.application.fusamate.repository.BrandRepository;
import com.application.fusamate.repository.criteria.BrandCriteriaRepository;
import com.application.fusamate.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    private final BrandCriteriaRepository brandCriteriaRepository;

    @Override
    public Brand createBrand(BrandDto brand) throws Exception {
        log.info("Created new brand: {}", brand);

        if (!brandRepository.findByNameLike(brand.getName().trim()).isEmpty()) {
            throw new Exception(Constants.DUPLICATED_BRAND);
        }

        Brand newBrand = new Brand();
        BeanUtils.copyProperties(brand, newBrand);
        newBrand.setName(brand.getName().trim());
        newBrand.setStatus(1);
        newBrand.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        return brandRepository.save(newBrand);
    }

    @Override
    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thương hiệu không tồn tại"));
    }

    @Override
    public Brand updateBrandById(UpdateBrandDto brandDto, Integer id) throws Exception {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thương hiệu không tồn tại"));

        if (!brandRepository.findByNameLike(brandDto.getName()).isEmpty()) {
            if (!brandDto.getName().trim().equalsIgnoreCase(brand.getName().trim()))
                throw new Exception(Constants.DUPLICATED_BRAND);
        }

        BeanUtils.copyProperties(brandDto, brand);

        brand.setName(brand.getName().trim());
        brand.setUpdatedAt(new Date());
        brand.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return brandRepository.save(brand);
    }

    @Override
    public Page<Brand> getBrands(BrandSearchCriteriaModel brandSearchCriteriaModel) {
        return brandCriteriaRepository.findAllWithFilters(brandSearchCriteriaModel);
    }

    @Override
    public List<Brand> getBrandsByStatus(int status) {
        return brandRepository.findByStatus(status);
    }
}
