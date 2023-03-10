package com.application.fusamate.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ProductDetailDto implements Serializable {
    @NotNull
    private final Integer availAmount;

    @NotNull
    private final Double originPrice;

    @NotNull
    private final Integer colorId;

    @NotNull
    private final Integer sizeId;

    @NotNull
    private final Integer madeInId;

}
