package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BrandSearchCriteriaModel extends FilterAndPagingModel {
    private String name;
    private Integer status;
}
