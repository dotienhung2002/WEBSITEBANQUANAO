package com.application.fusamate.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MadeInSaleDto implements Serializable {
    private Integer id;
    private String name;
}
