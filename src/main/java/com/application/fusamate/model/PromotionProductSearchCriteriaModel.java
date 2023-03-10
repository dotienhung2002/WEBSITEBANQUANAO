package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PromotionProductSearchCriteriaModel extends FilterAndPagingModel {
    private String name;
    private Integer percentageRange;
    private Boolean status;
    private Product product;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Product implements Serializable {
        private String name;
    }
}
