package com.application.fusamate.dto;

import com.application.fusamate.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductDto implements Serializable {
    @NotBlank
    private String name;
    @NotNull
    private Integer gender;
    @NotNull
    private Integer status;
    @NotBlank
    private String description;
    @NotNull
    private Integer totalAmount;
    @NotNull
    private Integer available;
    private Date createdAt;
    private Date updatedAt;
    private String updatedBy;
    private Category category;
    private Brand brand;
    private MadeIn madeIn;
    private List<ProductDetailDto> listProductDetail;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDetailDto implements Serializable {
        @NotNull
        private Double originPrice;
        @NotNull
        private Double promotionPercentage;
        @NotNull
        private Double promotionPrice;
        private Integer status;
        private Color color;
        private List<SizeDto> listSize;
        private List<ProductImage> listProductImage;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SizeDto implements Serializable {
            private Integer id;
            private String name;
            private String description;
            private Integer availAmount;
            private Long productDetailId;
        }
    }
}
