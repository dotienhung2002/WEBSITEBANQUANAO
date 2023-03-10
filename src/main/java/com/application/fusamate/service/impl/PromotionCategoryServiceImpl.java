package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.PromotionCategoryDto;
import com.application.fusamate.dto.UpdatePromotionCategoryDto;
import com.application.fusamate.entity.PromotionCategory;
import com.application.fusamate.model.PromotionCategorySearchCriteriaModel;
import com.application.fusamate.repository.*;
import com.application.fusamate.repository.criteria.PromotionCategoryCriteriaRepository;
import com.application.fusamate.service.PromotionCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionCategoryServiceImpl implements PromotionCategoryService {
    private final PromotionCategoryRepository promotionCategoryRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final CategoryRepository categoryRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;
    private final PromotionCategoryCriteriaRepository promotionCategoryCriteriaRepository;

    @Override
    public PromotionCategory getPromotionCategory(Integer id) {
        return promotionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khuyến mại tập sản phẩm không tồn tại"));
    }


    @Override
    @Transactional
    public PromotionCategory createPromotionCategory(PromotionCategoryDto promotionCategoryDto) throws Exception {
        if (!promotionCategoryRepository.findByNameLikeIgnoreCase(promotionCategoryDto.getName().trim()).isEmpty()) {
            throw new Exception(Constants.DUPLICATED_PROMOTION_CATEGORY);
        }
        if (promotionCategoryDto.getPercentage() < 1 || promotionCategoryDto.getPercentage() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phần trăm phải từ 1 - 100");
        }
        PromotionCategory promotionCategory = new PromotionCategory();
        promotionCategory.setName(promotionCategoryDto.getName());
        promotionCategory.setPercentage((float) Math.round(promotionCategoryDto.getPercentage() * 10) / 10);
        promotionCategory.setStatus(promotionCategoryDto.getStatus());
        promotionCategory.setDescription(promotionCategoryDto.getDescription());
        promotionCategory.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        promotionCategory.setCategory(categoryRepository.findById(promotionCategoryDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loại sản phẩm không tồn tại")));
        if (promotionCategory.getCategory().getStatus() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại sản phẩm không còn kinh doanh");

        if (promotionCategory.isStatus()) {
            // Tìm những khuyến mại đang acitve được áp dụng cho loại sp theo id được nhận
            List<PromotionCategory> activePromotionCategoryList = promotionCategoryRepository
                    .findByStatusAndCategory_Id(true, promotionCategoryDto.getCategoryId());
            // Cập nhật lại những khuyến mại đang active thành unactive
            if (!activePromotionCategoryList.isEmpty())
                activePromotionCategoryList.forEach(activePromotionCategory -> activePromotionCategory.setStatus(false));

            // cập nhật giá cho những sản phẩm cần được áp dụng với khuyến mại mới được tạo
            productRepository.findByCategory_Id(promotionCategory.getCategory().getId()).forEach(product -> {
                if (promotionProductRepository.findByStatusAndProduct_Id(true, product.getId()).isEmpty())
                    product.getListProductDetail().forEach(productDetail -> {
                        productDetail.setPromotionPercentage(promotionCategory.getPercentage());
                        productDetail.setPromotionPrice((float) Math.round(productDetail.getOriginPrice() *
                                (1 - (productDetail.getPromotionPercentage() / 100))));
                        productDetailRepository.save(productDetail);
                    });
            });
        }
        return promotionCategoryRepository.save(promotionCategory);
    }

    @Override
    @Transactional
    public PromotionCategory updatePromotionCategory(UpdatePromotionCategoryDto updatePromotionCategoryDto, Integer id) throws Exception {
        PromotionCategory promotionCategory = promotionCategoryRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khuyến mại loại sản phẩm không tồn tại"));
        if (promotionCategory.getCategory().getStatus() == 0)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể cập nhật do loại sản phẩm đã ngừng kinh doanh");
        if (!promotionCategoryRepository.findByNameLikeIgnoreCase(updatePromotionCategoryDto.getName().trim()).isEmpty()
                && !updatePromotionCategoryDto.getName().trim().equals(promotionCategory.getName().trim()))
            throw new Exception(Constants.DUPLICATED_PROMOTION_CATEGORY);
        if (updatePromotionCategoryDto.getPercentage() < 1 || updatePromotionCategoryDto.getPercentage() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phần trăm phải từ 1 đến 100");

        promotionCategory.setName(updatePromotionCategoryDto.getName());
        promotionCategory.setDescription(updatePromotionCategoryDto.getDescription());
        promotionCategory.setPercentage((float) Math.round(updatePromotionCategoryDto.getPercentage() * 10) / 10);
        promotionCategory.setStatus(updatePromotionCategoryDto.getStatus());

        // Cập nhật các thông tin nếu trạng thái khuyến mại lọai sản phẩm là 1. Đang sử dụng
        if (promotionCategory.isStatus()) {
            List<PromotionCategory> promotionCategoryList = promotionCategoryRepository
                    .findByStatusAndCategory_Id(true, promotionCategory.getCategory().getId());
            if (!promotionCategoryList.isEmpty())
                promotionCategoryList.forEach(promotionCategories -> {
                    if (!promotionCategories.getId().equals(id))
                        promotionCategories.setStatus(false);
                });
            promotionCategoryList.forEach(getPromotionCategory ->
                    productRepository.findByCategory_Id(getPromotionCategory.getCategory().getId()).forEach(product ->
                            {
                                if (promotionProductRepository.findByStatusAndProduct_Id(true, product.getId()).isEmpty())
                                    product.getListProductDetail().forEach(productDetail -> {
                                        productDetail.setPromotionPercentage(promotionCategory.getPercentage());
                                        productDetail.setPromotionPrice((float) Math.round(productDetail.getOriginPrice() *
                                                (1 - (productDetail.getPromotionPercentage() / 100))));
                                        productDetailRepository.save(productDetail);
                                    });
                            }
                    ));
        }

        if (!promotionCategory.isStatus()) {
            productRepository.findByCategory_Id(promotionCategory.getCategory().getId()).forEach(product -> {
                if (promotionProductRepository.findByStatusAndProduct_Id(true, product.getId()).isEmpty())
                    product.getListProductDetail().forEach(productDetail -> {
                        productDetail.setPromotionPercentage(0);
                        productDetail.setPromotionPrice(productDetail.getOriginPrice());
                        productDetailRepository.save(productDetail);
                    });
            });
        }
        return promotionCategoryRepository.save(promotionCategory);
    }

    @Override
    public Page<PromotionCategory> getPromotionCategories(PromotionCategorySearchCriteriaModel promotionCategorySearchCriteriaModel) {
        return promotionCategoryCriteriaRepository.findAllWithFilters(promotionCategorySearchCriteriaModel);
    }
}