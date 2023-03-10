package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategorySearchCriteriaModel extends FilterAndPagingModel{
    private String name;
    private Integer status;
    private ProductSet productSet;
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ProductSet implements Serializable {
        private Integer id;
    }
}
