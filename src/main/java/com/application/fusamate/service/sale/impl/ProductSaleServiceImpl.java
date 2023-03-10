package com.application.fusamate.service.sale.impl;

import com.application.fusamate.dto.sale.FindProductDto;
import com.application.fusamate.dto.sale.GetAllProductSaleDto;
import com.application.fusamate.entity.Brand;
import com.application.fusamate.entity.Category;
import com.application.fusamate.entity.MadeIn;
import com.application.fusamate.entity.Product;
import com.application.fusamate.repository.*;
import com.application.fusamate.service.sale.ProductSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSaleServiceImpl implements ProductSaleService {
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final SizeRepository sizeRepository;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final MadeInRepository madeInRepository;

    @Override
    public List<FindProductDto> findProducts(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);

        if (products.isEmpty()) return null;

        List<FindProductDto> findProductList = new ArrayList<>();

        if (products.size() >= 5) {
            for (int i = 0; i < 5; i++) {
                FindProductDto findProductDto = new FindProductDto();
                findProductDto.setId(products.get(i).getId());
                findProductDto.setName(products.get(i).getName());
                findProductDto.setOriginPrice(products.get(i).getListProductDetail().get(0).getOriginPrice());
                findProductDto.setPromotionPrice(products.get(i).getListProductDetail().get(0).getPromotionPrice());
                findProductDto.setColor(products.get(i).getListProductDetail().get(0).getColor());
                findProductDto.setImage(products.get(i).getListProductImage().isEmpty() ? "" : products.get(i).getListProductImage().get(0).getImage());
                findProductList.add(findProductDto);
            }
        } else {
            products.forEach(product -> {
                FindProductDto findProductDto = new FindProductDto();
                findProductDto.setId(product.getId());
                findProductDto.setName(product.getName());
                findProductDto.setOriginPrice(product.getListProductDetail().get(0).getOriginPrice());
                findProductDto.setPromotionPrice(product.getListProductDetail().get(0).getPromotionPrice());
                findProductDto.setColor(product.getListProductDetail().get(0).getColor());
                findProductDto.setImage(product.getListProductImage().isEmpty() ? "" : product.getListProductImage().get(0).getImage());
                findProductList.add(findProductDto);
            });
        }

        return findProductList;
    }

    @Override
    public List<GetAllProductSaleDto> getAllProducts() {
        List<GetAllProductSaleDto> getAllProductSaleDtoList = new ArrayList<>();

        List<Product> productList = productRepository.findAll();

        return convertProductListToDto(getAllProductSaleDtoList, productList);
    }

    private List<GetAllProductSaleDto> convertProductListToDto(List<GetAllProductSaleDto> getAllProductSaleDtoList, List<Product> productList) {
        productList.forEach(product -> {
            GetAllProductSaleDto getAllProductSaleDto = new GetAllProductSaleDto();
            getAllProductSaleDto.setId(product.getId());
            getAllProductSaleDto.setName(product.getName());
            getAllProductSaleDto.setGender(product.getGender());
            getAllProductSaleDto.setDescription(product.getDescription());
            getAllProductSaleDto.setCategory(product.getCategory());
            getAllProductSaleDto.setBrand(product.getBrand());
            getAllProductSaleDto.setMadeIn(product.getMadeIn());

            List<GetAllProductSaleDto.ProductDetailDto> productDetailDtoList = new ArrayList<>();
            List<Integer> listColorId = new ArrayList<>();
            product.getListProductDetail().forEach(productDetail -> {
                if (!listColorId.contains(productDetail.getColor().getId())) {
                    listColorId.add(productDetail.getColor().getId());
                    GetAllProductSaleDto.ProductDetailDto getProductDetailDto = new GetAllProductSaleDto.ProductDetailDto();
                    getProductDetailDto.setOriginPrice(productDetail.getOriginPrice());
                    getProductDetailDto.setPromotionPercentage(productDetail.getPromotionPercentage());
                    getProductDetailDto.setPromotionPrice(productDetail.getPromotionPrice());
                    getProductDetailDto.setStatus(productDetail.getStatus());
                    getProductDetailDto.setColor(productDetail.getColor());

                    List<GetAllProductSaleDto.ProductDetailDto.SizeDto> sizeDtoList = new ArrayList<>();
                    sizeRepository.findByListProductDetail_Product_IdAndListProductDetail_Color_Id(
                                    product.getId(), productDetail.getColor().getId())
                            .forEach(size -> sizeDtoList.add(new GetAllProductSaleDto.ProductDetailDto.
                                    SizeDto(size.getId(), size.getName(), size.getDescription(),
                                    productDetailRepository.findByProduct_IdAndColor_IdAndSize_Id(
                                            product.getId(), productDetail.getColor().getId(),
                                            size.getId()).getAvailAmount(),
                                    productDetailRepository.findByProduct_IdAndColor_IdAndSize_Id(product.getId(),
                                            productDetail.getColor().getId(), size.getId()).getId())));
                    getProductDetailDto.setListSize(sizeDtoList);
                    getProductDetailDto.setListProductImage(productImageRepository.
                            findByProduct_IdAndColor_Id(
                                    product.getId(), productDetail.getColor().getId()));
                    productDetailDtoList.add(getProductDetailDto);
                }
            });
            getAllProductSaleDto.setListProductDetail(productDetailDtoList);

            getAllProductSaleDtoList.add(getAllProductSaleDto);
        });

        return getAllProductSaleDtoList;
    }

    @Override
    public List<GetAllProductSaleDto> getAllActiveAndAvailableProducts() {
        List<GetAllProductSaleDto> getAllProductSaleDtoList = new ArrayList<>();

        List<Product> readyProducts = productRepository.findAll()
                .stream().filter(product -> product.getAvailable() == 1)
                .filter(product -> product.getStatus() == 1)
                .filter(product -> product.getTotalAmount() > 0)
                .filter(product -> product.getBrand().getStatus() == 1)
                .filter(product -> product.getCategory().getStatus() == 1)
                .collect(Collectors.toList());

        return convertProductListToDto(getAllProductSaleDtoList, readyProducts);
    }

    @Override
    public List<GetAllProductSaleDto> getAllActiveAndAvailableProductsByBrand(Integer brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu"));
        if (brand.getStatus() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thương hiệu không còn tồn tại");
        return this.getAllActiveAndAvailableProducts().stream()
                .filter(product -> product.getBrand().equals(brand))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetAllProductSaleDto> getAllActiveAndAvailableProductsByCategory(Integer cateId) {
        Category cate = categoryRepository.findById(cateId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy loại sản phẩm"));
        if (cate.getStatus() != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại sản phẩm không còn tồn tại");
        return this.getAllActiveAndAvailableProducts().stream()
                .filter(product -> product.getCategory().equals(cate))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetAllProductSaleDto> getAllActiveAndAvailableProductsByMadeIn(Integer madeInId) {
        MadeIn madeIn = madeInRepository.findById(madeInId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy xuất xứ"));
        return this.getAllActiveAndAvailableProducts().stream()
                .filter(product -> product.getMadeIn().equals(madeIn))
                .collect(Collectors.toList());
    }
}
