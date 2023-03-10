package com.application.fusamate.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BrandSaleDto implements Serializable {
    private Integer id;
    private String name;
}
