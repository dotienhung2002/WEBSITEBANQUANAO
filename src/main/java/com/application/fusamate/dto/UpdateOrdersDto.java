package com.application.fusamate.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UpdateOrdersDto implements Serializable {
    @NotBlank
    private final String name;
    @NotBlank
    private final String phone;
    @NotBlank
    private final String address;
    private final String note;
    @NotNull
    private final Integer status;
    private final Boolean paymentStatus;
}
