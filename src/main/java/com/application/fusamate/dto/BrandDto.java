package com.application.fusamate.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class BrandDto implements Serializable {
    private final String name;
    private final int status;
}
