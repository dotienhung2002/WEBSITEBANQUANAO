package com.application.fusamate.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UpdateProductDetailDto implements Serializable {
    @NotNull
    private final int availAmount;
    @NotNull
    private final double originPrice;
    @NotNull
    private final Integer madeInId;
    @NotNull
    private final Integer sizeId;
    @NotNull
    private final Integer colorId;
}
