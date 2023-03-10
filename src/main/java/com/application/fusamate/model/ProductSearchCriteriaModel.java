package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductSearchCriteriaModel extends FilterAndPagingModel {
    private String name;
    private Integer gender;
    private Brand brand;
    private Category category;
    private MadeIn madeIn;
    private Integer status;
    private Integer available;
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Brand implements Serializable {
        private Integer id;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Category implements Serializable {
        private Integer id;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class MadeIn implements Serializable {
        private Integer id;
    }
}
