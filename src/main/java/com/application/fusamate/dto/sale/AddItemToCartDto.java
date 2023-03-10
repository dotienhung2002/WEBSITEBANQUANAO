package com.application.fusamate.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddItemToCartDto {

    private String itemName;

    private String image;

    private Double price;

    private String color;

    private String size;

    private Integer quantity;

    private Long productDetailId;

}
