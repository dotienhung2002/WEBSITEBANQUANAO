package com.application.fusamate.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemAddedToCartDto implements Serializable {

    @NotBlank
    private String userAuthToken;

    @NotNull
    private Boolean registeredUser;

    @NotNull
    private Long productDetailId;

    @NotNull
    private Integer quantity;

}
