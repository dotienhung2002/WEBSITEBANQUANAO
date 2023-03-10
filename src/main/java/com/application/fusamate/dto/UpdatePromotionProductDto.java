package com.application.fusamate.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UpdatePromotionProductDto implements Serializable {
    @NotBlank
    private String name;

    @NotNull
    private Float percentage;

    @NotNull
    private Boolean status;

    private String description;
}
