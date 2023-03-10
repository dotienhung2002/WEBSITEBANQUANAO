package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.GetProductDto;
import com.application.fusamate.dto.ProductDto;
import com.application.fusamate.dto.UpdateProductDto;
import com.application.fusamate.entity.*;
import com.application.fusamate.model.ProductSearchCriteriaModel;
import com.application.fusamate.repository.*;
import com.application.fusamate.repository.criteria.ProductCriteriaRepository;
import com.application.fusamate.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductCriteriaRepository productCriteriaRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final MadeInRepository madeInRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final ProductImageRepository productImageRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionCategoryRepository promotionCategoryRepository;

    @Override
    @Transactional
    public Product createProduct(ProductDto productDto) throws Exception {
        Product addProduct = new Product();

        if (!productRepository.findByNameLikeIgnoreCase(productDto.getName().trim()).isEmpty())
            throw new Exception(Constants.DUPLICATED_PRODUCT);

        // Transfer input product's data 'productDto' to initialized product 'addProduct'
        addProduct.setName(productDto.getName());
        addProduct.setGender(productDto.getGender());
        addProduct.setDescription(productDto.getDescription());
        addProduct.setBrand(brandRepository.findById(productDto.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Th????ng hi???u kh??ng t???n t???i")));
        addProduct.setCategory(categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lo???i s???n ph???m kh??ng t???n t???i")));
        addProduct.setMadeIn(madeInRepository.findById(productDto.getMadeInId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Xu???t x??? kh??ng t???n t???i")));
        addProduct.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        // save product to get id auto-incremented from sql to set it to its product details
        Product addedProduct = productRepository.save(addProduct);

        // Create a list to collect all added product detail
        List<ProductDetail> productDetailList = new ArrayList<>();
        // Initialize a color list to check whether a color is chosen more than 1 time or not
        List<Integer> colorIdList = new ArrayList<>();
        // Transfer input product detail's data of 'productDto' to initialized product detail 'addProductDetail'
        productDto.getListProductDetail().forEach(productDetailDto -> {
            // Check the amount of size must be equal to the amount of avail amount
            if (productDetailDto.getListSizeIds().size() != productDetailDto.getListAvailAmount().size())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nh???p thi???u/th???a s??? l?????ng t???n cho t???ng k??ch c???");
            // Check duplicated color
            if (!colorIdList.contains(productDetailDto.getColorId())) {
                colorIdList.add(colorRepository.findById(productDetailDto.getColorId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "M??u s???c kh??ng t???n t???i")).getId());
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ch??? ???????c ch???n 1 m??u s???c cho m???i chi ti???t s???n ph???m");

            // Save images for each product detail
            productDetailDto.getListProductImage().forEach(productImageDto -> {
                ProductImage addProductImage = new ProductImage();
                addProductImage.setImage(productImageDto.getImage());
                addProductImage.setColor(colorRepository.findById(productDetailDto.getColorId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "M??u s???c kh??ng t???n t???i")));
                addProductImage.setProduct(addedProduct);

                productImageRepository.save(addProductImage);
            });

            // Initialize a size list to check whether a size is chosen more than 1 time or not
            List<Integer> sizeIdList = new ArrayList<>();
            // save product details based on list size
            productDetailDto.getListSizeIds().forEach(sizeId -> {
                // Check duplicated size
                if (!sizeIdList.contains(sizeId)) {
                    sizeIdList.add(sizeRepository.findById(sizeId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "K??ch c??? kh??ng t???n t???i")).getId());
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ch??? ???????c ch???n 1 k??ch c??? cho m???i chi ti???t s???n ph???m");

                ProductDetail addProductDetail = new ProductDetail();

                addProductDetail.setSize(sizeRepository.findById(sizeId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "K??ch c??? kh??ng t???n t???i")));
                addProductDetail.setColor(colorRepository.findById(productDetailDto.getColorId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "M??u s???c kh??ng t???n t???i")));

                // set size vs avail amount respectively
                addProductDetail.setAvailAmount(productDetailDto.getListAvailAmount()
                        .get(productDetailDto.getListSizeIds().indexOf(sizeId)));
                addProductDetail.setStatus(addProductDetail.getAvailAmount() > 0 ? 1 : 0);
                addProductDetail.setOriginPrice(productDetailDto.getOriginPrice());
                addProductDetail.setPromotionPrice(addProductDetail.getOriginPrice());

                //TODO: check whether added product belongs to a category that has a active promotion or not
                List<PromotionCategory> promotionCategoryList = promotionCategoryRepository.findByStatusAndCategory_Id(true, addedProduct.getCategory().getId());
                if (!promotionCategoryList.isEmpty()) {
                    addProductDetail.setPromotionPercentage(promotionCategoryList.get(0).getPercentage());
                    addProductDetail.setPromotionPrice((float) Math.round(addProductDetail.getOriginPrice() *
                            (1 - (addProductDetail.getPromotionPercentage() / 100)) * 10) / 10);
                }

                // set added product to product details' initialized product 'addProduct'
                addProductDetail.setProduct(addedProduct);

                // save product details of added product
                productDetailRepository.save(addProductDetail);

                // add product detail to created list
                productDetailList.add(addProductDetail);
            });
        });

        // set list of product details to added product's list of product details
        addedProduct.setListProductDetail(productDetailList);

        // c???p nh???t l???i c??c thu???c t??nh c???a s???n ph???m
        addedProduct.setTotalAmount(productDetailRepository.sumAvailAmountByProductId(addedProduct.getId()));
        addedProduct.setStatus(addedProduct.getTotalAmount() > 0 ? 1 : 0);
        return productRepository.save(addedProduct);
    }

    @Override
    public GetProductDto getProductById(Long id) {
        Product selectedProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "S???n ph???m kh??ng t???n t???i"));
        GetProductDto getProduct = new GetProductDto();
        getProduct.setName(selectedProduct.getName());
        getProduct.setGender(selectedProduct.getGender());
        getProduct.setStatus(selectedProduct.getStatus());
        getProduct.setDescription(selectedProduct.getDescription());
        getProduct.setTotalAmount(selectedProduct.getTotalAmount());
        getProduct.setAvailable(selectedProduct.getAvailable());
        getProduct.setCreatedAt(selectedProduct.getCreatedAt());
        getProduct.setUpdatedAt(selectedProduct.getUpdatedAt());
        getProduct.setUpdatedBy(selectedProduct.getUpdatedBy());

        getProduct.setCategory(selectedProduct.getCategory());
        getProduct.setBrand(selectedProduct.getBrand());
        getProduct.setMadeIn(selectedProduct.getMadeIn());

        List<GetProductDto.ProductDetailDto> getProductDetailDtoList = new ArrayList<>();
        List<Integer> listColorId = new ArrayList<>();
        selectedProduct.getListProductDetail().forEach(selectedProductDetail -> {
            if (!listColorId.contains(selectedProductDetail.getColor().getId())) {
                listColorId.add(selectedProductDetail.getColor().getId());
                GetProductDto.ProductDetailDto getProductDetailDto = new GetProductDto.ProductDetailDto();
                getProductDetailDto.setOriginPrice(selectedProductDetail.getOriginPrice());
                getProductDetailDto.setPromotionPercentage(selectedProductDetail.getPromotionPercentage());
                getProductDetailDto.setPromotionPrice(selectedProductDetail.getPromotionPrice());
                getProductDetailDto.setStatus(selectedProductDetail.getStatus());
                getProductDetailDto.setColor(selectedProductDetail.getColor());
                List<GetProductDto.ProductDetailDto.SizeDto> sizeDtoList = new ArrayList<>();
                sizeRepository.findByListProductDetail_Product_IdAndListProductDetail_Color_Id(
                                selectedProduct.getId(), selectedProductDetail.getColor().getId())
                        .forEach(size -> sizeDtoList.add(new GetProductDto.ProductDetailDto.
                                SizeDto(size.getId(), size.getName(), size.getDescription(),
                                productDetailRepository.findByProduct_IdAndColor_IdAndSize_Id(
                                        selectedProduct.getId(), selectedProductDetail.getColor().getId(),
                                        size.getId()).getAvailAmount(), productDetailRepository
                                .findByProduct_IdAndColor_IdAndSize_Id(id, selectedProductDetail.getColor().getId(), size.getId()).getId())));
                getProductDetailDto.setListSize(sizeDtoList);
                getProductDetailDto.setListProductImage(productImageRepository.
                        findByProduct_IdAndColor_Id(
                                selectedProduct.getId(), selectedProductDetail.getColor().getId()));
                getProductDetailDtoList.add(getProductDetailDto);
            }
        });

        getProduct.setListProductDetail(getProductDetailDtoList);

        return getProduct;
    }

    @Override
    @Transactional
    public Product updateProduct(UpdateProductDto updateProductDto, Long id) throws Exception {
        // find product by id
        Product updateProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "S???n ph???m kh??ng t???n t???i"));
        // check status of product
        if (updateProduct.getStatus() == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "S???n ph???m kh??ng th??? c???p nh???t do ???? ng???ng kinh doanh");
        // Check duplicated product
        if (!productRepository.findByNameLikeIgnoreCase(updateProductDto.getName().trim()).isEmpty()
                && !updateProduct.getName().equalsIgnoreCase(updateProductDto.getName()))
            throw new Exception(Constants.DUPLICATED_PRODUCT);
        // Initialize a color list to check whether a color is chosen more than 1 time or not
        List<Integer> colorIdList = new ArrayList<>();
        // TODO: Update each product detail
        updateProductDto.getListProductDetail().forEach(updateProductDetailDto -> {
            // Check the amount of size must be equal to the amount of avail amount
            if (updateProductDetailDto.getListSizeIds().size() != updateProductDetailDto.getListAvailAmount().size())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nh???p thi???u/th???a s??? l?????ng t???n cho t???ng k??ch c???");
            // Check duplicated color
            if (!colorIdList.contains(updateProductDetailDto.getColorId())) {
                colorIdList.add(colorRepository.findById(updateProductDetailDto.getColorId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "M??u s???c kh??ng t???n t???i")).getId());
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ch??? ???????c ch???n 1 m??u s???c cho m???i chi ti???t s???n ph???m");
            // Initialize a size list to check whether a size is chosen more than 1 time or not
            List<Integer> sizeIdList = new ArrayList<>();
            // Check duplicated size
            updateProductDetailDto.getListSizeIds().forEach(size -> {
                // Check duplicated size
                if (!sizeIdList.contains(size)) {
                    sizeIdList.add(sizeRepository.findById(size)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "K??ch c??? kh??ng t???n t???i")).getId());
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ch??? ???????c ch???n 1 k??ch c??? cho m???i chi ti???t s???n ph???m");

                // Transfer data to each product detail
                ProductDetail addOrUpdateProductDetail = new ProductDetail();
                ProductDetail existingProductDetail = productDetailRepository.findByProduct_IdAndColor_IdAndSize_Id(
                        updateProduct.getId(), updateProductDetailDto.getColorId(), size);
                if (existingProductDetail != null) {
                    // existing product detail
                    addOrUpdateProductDetail = existingProductDetail;
                }
                addOrUpdateProductDetail.setColor(colorRepository.findById(updateProductDetailDto.getColorId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "M??u s???c kh??ng t???n t???i")));
                addOrUpdateProductDetail.setSize(sizeRepository.findById(size)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "K??ch c??? kh??ng t???n t???i")));
                addOrUpdateProductDetail.setAvailAmount(updateProductDetailDto.getListAvailAmount()
                        .get(updateProductDetailDto.getListSizeIds().indexOf(size)));
                addOrUpdateProductDetail.setStatus(addOrUpdateProductDetail.getAvailAmount() > 0 ? 1 : 0);
                addOrUpdateProductDetail.setOriginPrice(updateProductDetailDto.getOriginPrice());
                addOrUpdateProductDetail.setPromotionPrice(addOrUpdateProductDetail.getOriginPrice());
                addOrUpdateProductDetail.setProduct(updateProduct);

                productDetailRepository.save(addOrUpdateProductDetail);

                //TODO: Check where the product is applied for a promotion product or not - DONE
                if (!promotionProductRepository.
                        findByStatusAndProduct_Id(true, updateProduct.getId()).isEmpty()) {
                    PromotionProduct promotionProduct = promotionProductRepository.
                            findByStatusAndProduct_Id(true, updateProduct.getId()).get(0);
                    addOrUpdateProductDetail.setPromotionPercentage(promotionProduct.getPercentage());
                    addOrUpdateProductDetail.setPromotionPrice((float) Math.round(addOrUpdateProductDetail.getOriginPrice() *
                            (1 - (addOrUpdateProductDetail.getPromotionPercentage() / 100)) * 10) / 10);

                    productDetailRepository.save(addOrUpdateProductDetail);
                } else if (!promotionCategoryRepository.
                        findByStatusAndCategory_Id(true, updateProductDto.getCategoryId()).isEmpty()) {
                    //TODO::PromotionCategory: check whether added product belongs to a category that has a active promotion or not
                    PromotionCategory promotionCategory = promotionCategoryRepository.
                            findByStatusAndCategory_Id(true, updateProductDto.getCategoryId()).get(0);
                    addOrUpdateProductDetail.setPromotionPercentage(promotionCategory.getPercentage());
                    addOrUpdateProductDetail.setPromotionPrice((float) Math.round(addOrUpdateProductDetail.getOriginPrice() *
                            (1 - (addOrUpdateProductDetail.getPromotionPercentage() / 100)) * 10) / 10);

                    productDetailRepository.save(addOrUpdateProductDetail);
                }
            });
            // Delete images for each product detail
            productImageRepository.deleteAll(productImageRepository
                    .findByProduct_IdAndColor_Id(updateProduct.getId(), updateProductDetailDto.getColorId()));
            // Save images for each product detail
            updateProductDetailDto.getListProductImage().forEach(productImageDto -> {
                ProductImage addOrUpdateProductImage = new ProductImage();
                addOrUpdateProductImage.setImage(productImageDto.getImage());
                addOrUpdateProductImage.setColor(colorRepository.findById(updateProductDetailDto.getColorId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "M??u s???c kh??ng t???n t???i")));
                addOrUpdateProductImage.setProduct(updateProduct);
                productImageRepository.save(addOrUpdateProductImage);
            });
        });

        // update selected product
        updateProduct.setName(updateProductDto.getName());
        updateProduct.setGender(updateProductDto.getGender());
        updateProduct.setDescription(updateProductDto.getDescription());
        updateProduct.setBrand(brandRepository.findById(updateProductDto.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Th????ng hi???u kh??ng t???n t???i")));
        updateProduct.setCategory(categoryRepository.findById(updateProductDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lo???i s???n ph???m kh??ng t???n t???i")));
        updateProduct.setMadeIn(madeInRepository.findById(updateProductDto.getMadeInId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Xu???t x??? kh??ng t???n t???i")));
        updateProduct.setTotalAmount(productDetailRepository.sumAvailAmountByProductId(updateProduct.getId()));
        updateProduct.setStatus(updateProduct.getTotalAmount() > 0 ? 1 : 0);
        // make sure that only the status of -1. Stop business is chosen
        updateProduct.setStatus(updateProductDto.getStatus() == -1 ? updateProductDto.getStatus() : updateProduct.getStatus());
        updateProduct.setAvailable(updateProductDto.getAvailable() == 1 || updateProductDto.getAvailable() == 0 ? updateProductDto.getAvailable() : updateProduct.getAvailable());
        updateProduct.setUpdatedAt(new Date());
        updateProduct.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return productRepository.save(updateProduct);
    }

    @Override
    public Page<Product> getProducts(ProductSearchCriteriaModel productSearchCriteriaModel) {
        return productCriteriaRepository.findAllWithFilters(productSearchCriteriaModel);
    }

    @Override
    public Boolean updateAvailableById(Integer available, Long id) {
        Product updateProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "S???n ph???m kh??ng t???n t???i"));
        updateProduct.setUpdatedAt(new Date());
        updateProduct.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        updateProduct.setAvailable(available);
        return productRepository.save(updateProduct).getAvailable() == 1;
    }
}
