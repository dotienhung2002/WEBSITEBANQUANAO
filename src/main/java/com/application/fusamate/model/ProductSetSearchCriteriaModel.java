package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductSetSearchCriteriaModel extends FilterAndPagingModel {
    private String name;
    private Integer status;
}
