package com.application.fusamate.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetItemFromCartDto implements Serializable {

    @NotBlank
    private String userAuthToken;

}
