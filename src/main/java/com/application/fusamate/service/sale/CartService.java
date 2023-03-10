package com.application.fusamate.service.sale;

import com.application.fusamate.dto.sale.*;
import com.application.fusamate.entity.CartGeneral;

public interface CartService {

    CartDto getCart(String userAuthToken);

    AddItemToCartDto addItemToCart(ItemAddedToCartDto itemAddedToCartDto);

    CartGeneral updateItemQuantityToCart(ItemQuantityUpdateToCartDto itemQuantityUpdateToCartDto);

    CartGeneral removeItemFromCart(ItemRemovedFromCartDto itemRemovedFromCartDto);

    void removeAllFromCart(String userAuthToken);

}
