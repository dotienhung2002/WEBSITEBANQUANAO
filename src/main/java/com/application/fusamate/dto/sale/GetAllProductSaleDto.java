package com.application.fusamate.dto.sale;

import com.application.fusamate.entity.*;
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
public class GetAllProductSaleDto implements Serializable {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private int gender;
    @NotBlank
    private String description;
    private Category category;
    private Brand brand;
    private MadeIn madeIn;
    private List<ProductDetailDto> listProductDetail;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDetailDto implements Serializable {
        @NotNull
        private double originPrice;
        @NotNull
        private double promotionPercentage;
        @NotNull
        private double promotionPrice;
        private int status;
        private Color color;
        private List<SizeDto> listSize;
        private List<ProductImage> listProductImage;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SizeDto implements Serializable {
            private Integer id;
            @NotBlank
            private String name;
            private String description;
            private Integer availAmount;
            private Long productDetailId;
        }
    }
}
