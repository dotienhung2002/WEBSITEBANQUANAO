package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.ProductDetailDto;
import com.application.fusamate.dto.UpdateProductDetailDto;
import com.application.fusamate.entity.Product;
import com.application.fusamate.entity.ProductDetail;
import com.application.fusamate.entity.PromotionProduct;
import com.application.fusamate.model.ProductDetailSearchCriteriaModel;
import com.application.fusamate.repository.*;
import com.application.fusamate.repository.criteria.ProductDetailCriteriaRepository;
import com.application.fusamate.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductRepository productRepository;
    private final ProductDetailCriteriaRepository productDetailCriteriaRepository;
    private final ProductDetailRepository productDetailRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final PromotionProductRepository promotionProductRepository;

    @Override
    @Transactional
    public ProductDetail createProductDetail(Long productId, ProductDetailDto productDetailDto) throws Exception {
        // Lấy ra sản phẩm có chi tiết cần thêm
        Product selectedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));

        // Check xem chi tiết sản phẩm đã có 3 thuộc tính tồn tại hay chưa?
        if (!selectedProduct.getListProductDetail().stream()
                .filter(productDetail -> productDetail.getSize().getId().equals(productDetailDto.getSizeId()))
                .filter(productDetail -> productDetail.getColor().getId().equals(productDetailDto.getColorId()))
                .collect(Collectors.toList()).isEmpty()) {
            throw new Exception(Constants.DUPLICATED_PRODUCT_DETAIL);
        }

        if (productDetailDto.getOriginPrice() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá ban đầu phải lớn hơn hoặc bằng 0");
        if (productDetailDto.getAvailAmount() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng tồn phải lớn hơn hoặc bằng 0");

        // Tạo ra chi tiết sản phẩm mới để truyền dữ liệu cần thêm vào
        ProductDetail createProductDetail = new ProductDetail();
        createProductDetail.setSize(sizeRepository.findById(productDetailDto.getSizeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kích cỡ không tồn tại")));
        createProductDetail.setColor(colorRepository.findById(productDetailDto.getColorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Màu sắc không tồn tại")));
        createProductDetail.setOriginPrice(productDetailDto.getOriginPrice());
        createProductDetail.setAvailAmount(productDetailDto.getAvailAmount());

        // cập nhật lại các thuộc tính của sản phẩm
        selectedProduct.setTotalAmount(productDetailRepository
                .sumAvailAmountByProductId(selectedProduct.getId()) + createProductDetail.getAvailAmount());
        selectedProduct.setUpdatedAt(new Date());
        selectedProduct.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        // Lưu lại thông tin của sản phẩm được chọn
        productRepository.save(selectedProduct);

        /*
        * TODO: Do status phụ thuộc vào tổng số lượng tồn cần lấy trong database nên cần lưu thông tin rồi check lại status
        * rồi lưu lại tiếp
        * */
        selectedProduct.setStatus(selectedProduct.getTotalAmount() > 0 ? 1 : 0);

        productRepository.save(selectedProduct);

        // TODO: Need to check promotion product and PROMOTION CATEGORY
        List<PromotionProduct> activePromotionProducts = promotionProductRepository.findByStatusAndProduct_Id(true, productId);
        if (!activePromotionProducts.isEmpty()) {
            activePromotionProducts.get(0).getProduct().getListProductDetail().forEach(productDetail -> {
                productDetail.setPromotionPercentage(activePromotionProducts.get(0).getPercentage());
                productDetail.setPromotionPrice((double) Math.round(productDetail.getOriginPrice() *
                        (1 - productDetail.getPromotionPercentage() / 100) * 10) / 10);
            });
            createProductDetail.setPromotionPercentage(activePromotionProducts.get(0).getPercentage());
            createProductDetail.setPromotionPrice((double) Math.round(createProductDetail.getOriginPrice() *
                    (1 - createProductDetail.getPromotionPercentage() / 100) * 10) / 10);
        } else {
            createProductDetail.setPromotionPercentage(0);
            createProductDetail.setPromotionPrice(createProductDetail.getOriginPrice());
        }

        createProductDetail.setProduct(selectedProduct);

        return productDetailRepository.save(createProductDetail);
    }

    @Override
    public ProductDetail getProductDetailById(Long id) {
        return productDetailRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chi tiết sản phẩm không tồn tại"));
    }

    @Override
    public ProductDetail updateProductDetailById(UpdateProductDetailDto updateProductDetailDto, Long id) throws Exception {
        ProductDetail updateProductDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chi tiết sản phẩm không tồn tại"));
        /*
        * Check xem các thuộc tính đầu vào có giống hệt với các thuộc tính chi tiết sản phẩm cần được update hay không để
        * tránh bị check trùng chính nó
        * */
        if (!updateProductDetail.getSize().getId().equals(updateProductDetailDto.getSizeId()) ||
                !updateProductDetail.getColor().getId().equals(updateProductDetailDto.getColorId())) {
            /*
            * Nếu như thuộc tính khác với các thuộc tính chi tiết sản phẩm cần được update thì sẽ check xem liệu đã có
            * một chi tiết sản phẩm nào đã có cả 3 thuộc tính đầu vào hay chưa
            * */
            if (!updateProductDetail.getProduct().getListProductDetail().stream()
                    .filter(productDetail -> productDetail.getSize().getId().equals(updateProductDetailDto.getSizeId()))
                    .filter(productDetail -> productDetail.getColor().getId().equals(updateProductDetailDto.getColorId()))
                    .collect(Collectors.toList()).isEmpty()) {
                throw new Exception(Constants.DUPLICATED_PRODUCT_DETAIL);
            }
            updateProductDetail.setSize(sizeRepository.findById(updateProductDetailDto.getSizeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kích cỡ không tồn tại")));
            updateProductDetail.setColor(colorRepository.findById(updateProductDetailDto.getColorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Màu sắc không tồn tại")));
        }

        // Cập nhật các thuộc tính của chi tiết sản phẩm
        updateProductDetail.setOriginPrice(updateProductDetailDto.getOriginPrice());
        updateProductDetail.setAvailAmount(updateProductDetailDto.getAvailAmount());
        updateProductDetail.setPromotionPrice((double) Math.round(updateProductDetail.getOriginPrice() *
                (1 - updateProductDetail.getPromotionPercentage() / 100) * 10) / 10);

        // Lưu lại các thông tin của chi tiết sp
        ProductDetail updatedProductDetail = productDetailRepository.save(updateProductDetail);

        // Lấy ra sản phẩm mà chi tiết sp cần được update
        Product updateProduct = updatedProductDetail.getProduct();

        // Cập nhật các thông tin của sản phẩm mà chi tiết sp đã được update
        updateProduct.setTotalAmount(
                productDetailRepository.sumAvailAmountByProductId(updatedProductDetail.getProduct().getId()));
        updateProduct.setUpdatedAt(new Date());
        updateProduct.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        // Lưu lại sản phẩm có chi tiết sp update
        productRepository.save(updateProduct);

        /*
        * TODO: Do status phụ thuộc vào tổng số lượng tồn cần lấy trong database nên cần lưu thông tin rồi check lại status
        rồi lưu lại tiếp
        * */
        updateProduct.setStatus(updateProduct.getTotalAmount() > 0 ? 1 : 0);

        productRepository.save(updateProduct);

        return updatedProductDetail;
    }

    @Override
    public Page<ProductDetail> getProductDetailsByProductId(Long productId,
                                                            ProductDetailSearchCriteriaModel
                                                                    productDetailSearchCriteriaModel) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));
        return productDetailCriteriaRepository.findAllWithFilters(productId, productDetailSearchCriteriaModel);
    }
}
