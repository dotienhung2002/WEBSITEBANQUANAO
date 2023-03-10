package com.application.fusamate.dto.sale;

import com.application.fusamate.entity.Color;
import com.application.fusamate.entity.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto implements Serializable {
    @NotNull
    private Long id;
    @NotBlank
    private String userAuthToken;
    @NotNull
    private boolean registeredUser;
    private List<CartProductDto> cartProducts;

    @Data
    public static class CartProductDto implements Serializable {
        @NotBlank
        private String productName;
        @NotNull
        private Integer quantity;
        @NotNull
        private Long productDetailId;
        @NotNull
        private ProductDetailDto productDetail;

        @Data
        public static class ProductDetailDto implements Serializable {
            @NotNull
            private Double originPrice;
            @NotNull
            private Double promotionPercentage;
            @NotNull
            private Double promotionPrice;
            @NotNull
            private Long productId;
            @NotNull
            private Size size;
            @NotNull
            private Color color;
            private List<ProductImageDto> listProductImage;

            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public static class ProductImageDto implements Serializable {
                @NotBlank
                private String image;
            }
        }
    }
}
