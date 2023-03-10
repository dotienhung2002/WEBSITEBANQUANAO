package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PromotionCategorySearchCriteriaModel extends FilterAndPagingModel {
    private String name;
    private Integer percentageRange;
    private Boolean status;
    private Category category;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Category implements Serializable {
        private String name;
    }
}
