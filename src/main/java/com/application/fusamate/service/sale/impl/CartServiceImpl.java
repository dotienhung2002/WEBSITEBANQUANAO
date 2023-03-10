package com.application.fusamate.service.sale.impl;

import com.application.fusamate.dto.sale.*;
import com.application.fusamate.entity.CartGeneral;
import com.application.fusamate.entity.CartProduct;
import com.application.fusamate.entity.ProductDetail;
import com.application.fusamate.repository.CartGeneralRepository;
import com.application.fusamate.repository.CartProductRepository;
import com.application.fusamate.repository.ProductDetailRepository;
import com.application.fusamate.service.sale.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartGeneralRepository cartGeneralRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductDetailRepository productDetailRepository;

    @Override
    public CartDto getCart(String userAuthToken) {
        CartGeneral cartGeneral = cartGeneralRepository.findByUserAuthToken(userAuthToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"));
        CartDto cartDto = new CartDto();
        cartDto.setId(cartGeneral.getId());
        cartDto.setUserAuthToken(cartGeneral.getUserAuthToken());
        cartDto.setRegisteredUser(cartGeneral.isRegisteredUser());
        List<CartDto.CartProductDto> cartProductDtoList = new ArrayList<>();
        cartGeneral.getCartProducts().forEach(cartProduct -> {
            CartDto.CartProductDto newCartProductDto = new CartDto.CartProductDto();
            newCartProductDto.setProductName(cartProduct.getProductDetail().getProduct().getName());
            newCartProductDto.setQuantity(cartProduct.getQuantity());

            ProductDetail productDetail = productDetailRepository.findById(cartProduct.getProductDetail().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));
            newCartProductDto.setProductDetailId(productDetail.getId());
            CartDto.CartProductDto.ProductDetailDto productDetailDto = new CartDto.CartProductDto.ProductDetailDto();
            productDetailDto.setOriginPrice(productDetail.getOriginPrice());
            productDetailDto.setPromotionPercentage(productDetail.getPromotionPercentage());
            productDetailDto.setPromotionPrice(productDetail.getPromotionPrice());
            productDetailDto.setProductId(productDetail.getProduct().getId());
            productDetailDto.setSize(productDetail.getSize());
            productDetailDto.setColor(productDetail.getColor());

            List<CartDto.CartProductDto.ProductDetailDto.ProductImageDto> productImageDtoList = new ArrayList<>();
            productDetail.getProduct().getListProductImage().stream()
                    .filter(productImage -> productImage.getColor().equals(cartProduct.getProductDetail().getColor()))
                    .forEach(productImage -> productImageDtoList.add(
                            new CartDto.CartProductDto.ProductDetailDto.ProductImageDto(productImage.getImage())));

            productDetailDto.setListProductImage(productImageDtoList);

            newCartProductDto.setProductDetail(productDetailDto);

            cartProductDtoList.add(newCartProductDto);
        });

        cartDto.setCartProducts(cartProductDtoList);

        return cartDto;
    }

    @Override
    @Transactional
    public AddItemToCartDto addItemToCart(ItemAddedToCartDto itemAddedToCartDto) {
        Optional<CartGeneral> existedCart = cartGeneralRepository.findByUserAuthToken(itemAddedToCartDto.getUserAuthToken());

        if (existedCart.isEmpty()) {
            log.info(">>> new Cart");
            CartGeneral newCartGeneral = new CartGeneral();
            newCartGeneral.setRegisteredUser(itemAddedToCartDto.getRegisteredUser());
            newCartGeneral.setUserAuthToken(itemAddedToCartDto.getUserAuthToken());
            cartGeneralRepository.save(newCartGeneral);

            CartProduct newCartProduct = new CartProduct();
            newCartProduct.setProductDetail(productDetailRepository.findById(itemAddedToCartDto.getProductDetailId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại")));
            newCartProduct.setQuantity(itemAddedToCartDto.getQuantity());
            newCartProduct.setCartGeneral(newCartGeneral);
            cartProductRepository.save(newCartProduct);

            AddItemToCartDto addItemToCartDto = new AddItemToCartDto();
            addItemToCartDto.setItemName(newCartProduct.getProductDetail().getProduct().getName());
            addItemToCartDto.setImage(newCartProduct.getProductDetail().getProduct()
                    .getListProductImage().stream()
                    .filter(productImage -> productImage.getColor().equals(newCartProduct.getProductDetail().getColor()))
                    .collect(Collectors.toList()).get(0).getImage());
            addItemToCartDto.setPrice(newCartProduct.getProductDetail().getPromotionPrice());
            addItemToCartDto.setSize(newCartProduct.getProductDetail().getSize().getName());
            addItemToCartDto.setColor(newCartProduct.getProductDetail().getColor().getName());
            addItemToCartDto.setQuantity(newCartProduct.getQuantity());
            addItemToCartDto.setProductDetailId(newCartProduct.getProductDetail().getId());

            return addItemToCartDto;
        }

        log.info(">>> existed Cart");
        CartGeneral existedCartGeneral = existedCart.get();
        existedCartGeneral.setUserAuthToken(itemAddedToCartDto.getUserAuthToken());
        existedCartGeneral.setRegisteredUser(itemAddedToCartDto.getRegisteredUser());
        cartGeneralRepository.save(existedCartGeneral);

        Optional<CartProduct> existedCartProduct = existedCartGeneral.getCartProducts().stream().filter(
                cartProduct -> cartProduct.getProductDetail().getId().equals(itemAddedToCartDto.getProductDetailId()))
                .findFirst();
        existedCartProduct.ifPresent(cartProduct ->
                cartProduct.setQuantity(cartProduct.getQuantity() + itemAddedToCartDto.getQuantity()));

        AddItemToCartDto addItemToCartDto = new AddItemToCartDto();

        if (existedCartProduct.isEmpty()) {
            CartProduct newCartProduct = new CartProduct();
            newCartProduct.setProductDetail(productDetailRepository.findById(itemAddedToCartDto.getProductDetailId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại")));
            newCartProduct.setQuantity(itemAddedToCartDto.getQuantity());
            newCartProduct.setCartGeneral(existedCartGeneral);
            cartProductRepository.save(newCartProduct);

            addItemToCartDto.setItemName(newCartProduct.getProductDetail().getProduct().getName());
            addItemToCartDto.setImage(newCartProduct.getProductDetail().getProduct()
                    .getListProductImage().stream()
                    .filter(productImage -> productImage.getColor().equals(newCartProduct.getProductDetail().getColor()))
                    .collect(Collectors.toList()).get(0).getImage());
            addItemToCartDto.setPrice(newCartProduct.getProductDetail().getPromotionPrice());
            addItemToCartDto.setSize(newCartProduct.getProductDetail().getSize().getName());
            addItemToCartDto.setColor(newCartProduct.getProductDetail().getColor().getName());
            addItemToCartDto.setQuantity(newCartProduct.getQuantity());
            addItemToCartDto.setProductDetailId(newCartProduct.getProductDetail().getId());
        } else {
            cartProductRepository.save(existedCartProduct.get());

            addItemToCartDto.setItemName(existedCartProduct.get().getProductDetail().getProduct().getName());
            addItemToCartDto.setImage(existedCartProduct.get().getProductDetail().getProduct()
                    .getListProductImage().stream()
                    .filter(productImage -> productImage.getColor().equals(existedCartProduct.get().getProductDetail().getColor()))
                    .collect(Collectors.toList()).get(0).getImage());
            addItemToCartDto.setPrice(existedCartProduct.get().getProductDetail().getPromotionPrice());
            addItemToCartDto.setSize(existedCartProduct.get().getProductDetail().getSize().getName());
            addItemToCartDto.setColor(existedCartProduct.get().getProductDetail().getColor().getName());
            addItemToCartDto.setQuantity(existedCartProduct.get().getQuantity());
            addItemToCartDto.setProductDetailId(existedCartProduct.get().getProductDetail().getId());
        }

        return addItemToCartDto;
    }

    @Override
    @Transactional
    public CartGeneral updateItemQuantityToCart(ItemQuantityUpdateToCartDto itemQuantityUpdateToCartDto) {
        CartGeneral cartGeneral = cartGeneralRepository.findByUserAuthToken(itemQuantityUpdateToCartDto.getUserAuthToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"));
        Optional<CartProduct> cartProduct = cartProductRepository.
                findByCartGeneral_UserAuthTokenAndProductDetail_Id(
                        itemQuantityUpdateToCartDto.getUserAuthToken(), itemQuantityUpdateToCartDto.getProductDetailId());
        if (cartProduct.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        cartProduct.get().setQuantity(itemQuantityUpdateToCartDto.getQuantity() >= 0 ?
                itemQuantityUpdateToCartDto.getQuantity() : cartProduct.get().getQuantity());
        cartProductRepository.save(cartProduct.get());
        cartGeneral.getCartProducts().stream().filter(item -> item.getQuantity() == 0)
                .forEach(cartProductRepository::delete);
        return cartGeneral;
    }

    @Override
    @Transactional
    public CartGeneral removeItemFromCart(ItemRemovedFromCartDto itemRemovedFromCartDto) {
        if (cartGeneralRepository.findByUserAuthToken(itemRemovedFromCartDto.getUserAuthToken()).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng");
        cartProductRepository.deleteByProductDetail(
                productDetailRepository.findById(itemRemovedFromCartDto.getProductDetailId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm")));
        return cartGeneralRepository.findByUserAuthToken(itemRemovedFromCartDto.getUserAuthToken()).get();
    }

    @Override
    @Transactional
    public void removeAllFromCart(String userAuthToken) {
        CartGeneral cartGeneral = cartGeneralRepository.findByUserAuthToken(userAuthToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"));
        cartProductRepository.deleteByCartGeneral(cartGeneral);
        cartGeneralRepository.deleteByUserAuthToken(userAuthToken);
    }
}
