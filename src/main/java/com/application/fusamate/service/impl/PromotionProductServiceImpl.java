package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.PromotionProductDto;
import com.application.fusamate.dto.UpdatePromotionProductDto;
import com.application.fusamate.entity.PromotionCategory;
import com.application.fusamate.entity.PromotionProduct;
import com.application.fusamate.model.PromotionProductSearchCriteriaModel;
import com.application.fusamate.repository.ProductRepository;
import com.application.fusamate.repository.PromotionCategoryRepository;
import com.application.fusamate.repository.PromotionProductRepository;
import com.application.fusamate.repository.criteria.PromotionProductCriteriaRepository;
import com.application.fusamate.service.PromotionProductService;
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
public class PromotionProductServiceImpl implements PromotionProductService {
    private final PromotionProductRepository promotionProductRepository;
    private final ProductRepository productRepository;
    private final PromotionProductCriteriaRepository promotionProductCriteriaRepository;
    private final PromotionCategoryRepository promotionCategoryRepository;

    @Override
    public PromotionProduct getPromotionProduct(Long id) {
        return promotionProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion Product does not exist!"));
    }

    @Override
    @Transactional
    public PromotionProduct createPromotionProduct(PromotionProductDto promotionProductDto) throws Exception {
        if (promotionProductRepository.findByName(promotionProductDto.getName().trim()) != null) {
            throw new Exception(Constants.DUPLICATED_PROMOTION_PRODUCT);
        }
        if (promotionProductDto.getPercentage() < 1 || promotionProductDto.getPercentage() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phần trăm phải từ 1 đến 100");
        }

        PromotionProduct promotionProduct = new PromotionProduct();
        promotionProduct.setName(promotionProductDto.getName());
        promotionProduct.setPercentage((float) Math.round(promotionProductDto.getPercentage() * 10) / 10);
        promotionProduct.setStatus(promotionProductDto.getStatus());
        promotionProduct.setDescription(promotionProductDto.getDescription());
        promotionProduct.setProduct(productRepository.findById(promotionProductDto.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại")));

        if (promotionProduct.isStatus()) {
            //Lấy các khuyến mãi mà có trạng thái là đang sử dụng và có id sản phẩm tồn tại trong db
            List<PromotionProduct> promotionProductList = promotionProductRepository.
                    findByStatusAndProduct_Id(true, promotionProductDto.getProductId());
            if (!promotionProductList.isEmpty()) {
                promotionProductList.forEach(promotionProductTemp -> {
                    promotionProductTemp.setStatus(false);
                });
            }

            promotionProduct.getProduct().getListProductDetail().forEach(productDetail -> {
                productDetail.setPromotionPercentage(promotionProductDto.getPercentage());
                productDetail.setPromotionPrice((float) Math.round(productDetail.getOriginPrice() *
                        (1 - (productDetail.getPromotionPercentage() / 100)) * 10) / 10);
            });
        }
        promotionProduct.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        return promotionProductRepository.save(promotionProduct);
    }

    @Override
    @Transactional
    public PromotionProduct updatePromotionProduct(UpdatePromotionProductDto updatePromotionProductDto, Long id) throws Exception {
        PromotionProduct updatePromotionProduct = promotionProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khuyến mại sản phẩm không tồn tại"));

        if (promotionProductRepository.findByName(updatePromotionProductDto.getName().trim()) != null &&
                !updatePromotionProductDto.getName().trim().equals(updatePromotionProduct.getName().trim())) {
            throw new Exception(Constants.DUPLICATED_PROMOTION_PRODUCT);
        }
        if (updatePromotionProductDto.getPercentage() < 1 || updatePromotionProductDto.getPercentage() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phần trăm phải từ 1 đến 100");
        }
        updatePromotionProduct.setName(updatePromotionProductDto.getName().trim());
        updatePromotionProduct.setDescription(updatePromotionProductDto.getDescription().trim());
        updatePromotionProduct.setPercentage((float) Math.round(updatePromotionProductDto.getPercentage() * 10) / 10);
        updatePromotionProduct.setStatus(updatePromotionProductDto.getStatus());
        if (updatePromotionProduct.isStatus()) {
            //Lấy các khuyến mãi có trạng thái là đang sử dụng và có id sản phẩm tồn tại trong database
            List<PromotionProduct> activePromotionList = promotionProductRepository
                    .findByStatusAndProduct_Id(true, updatePromotionProduct.getProduct().getId());
            if (!activePromotionList.isEmpty()) {
                activePromotionList.stream().filter(activePromotion -> !activePromotion.getId().equals(updatePromotionProduct.getId()))
                        .forEach(otherActivePromotion -> otherActivePromotion.setStatus(false));
            }
            updatePromotionProduct.getProduct().getListProductDetail().forEach(productDetail -> {
                productDetail.setPromotionPercentage(updatePromotionProductDto.getPercentage());
                productDetail.setPromotionPrice((float) Math.round(productDetail.getOriginPrice() *
                        (1 - (productDetail.getPromotionPercentage() / 100)) * 10) / 10);
            });
        }
        if (!updatePromotionProduct.isStatus()) {
            updatePromotionProduct.getProduct().getListProductDetail().forEach(productDetail -> {
                // TODO: Kiểm tra nếu sản phẩm có nằm trong loại sản phẩm nào đang có khuyến mại hay không
                if (!promotionCategoryRepository.findByStatusAndCategory_Id(true,
                        updatePromotionProduct.getProduct().getCategory().getId()).isEmpty()) {
                    PromotionCategory promotionCategory = promotionCategoryRepository.findByStatusAndCategory_Id(true,
                            updatePromotionProduct.getProduct().getCategory().getId()).get(0);
                    productDetail.setPromotionPercentage(promotionCategory.getPercentage());
                    productDetail.setPromotionPrice((float) Math.round(productDetail.getOriginPrice() *
                            (1 - (productDetail.getPromotionPercentage() / 100)) * 10) / 10);
                } else {
                    productDetail.setPromotionPercentage(0);
                    productDetail.setPromotionPrice(productDetail.getOriginPrice());
                }
            });
        }
        return promotionProductRepository.save(updatePromotionProduct);
    }

    @Override
    public Page<PromotionProduct> getPromotionProducts(PromotionProductSearchCriteriaModel promotionProductSearchCriteriaModel) {
        return promotionProductCriteriaRepository.findAllWithFilters(promotionProductSearchCriteriaModel);
    }
}
