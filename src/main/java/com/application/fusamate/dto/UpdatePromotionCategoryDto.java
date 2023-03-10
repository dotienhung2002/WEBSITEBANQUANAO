package com.application.fusamate.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UpdatePromotionCategoryDto implements Serializable {
    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Boolean status;

    @NotNull
    private Float percentage;

}
