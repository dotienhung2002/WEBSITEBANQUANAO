package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailSearchCriteriaModel extends FilterAndPagingModel implements Serializable {
    private Double startPrice;
    private Double endPrice;
    private MadeInDto madeIn;
    private SizeDto size;
    private ColorDto color;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MadeInDto implements Serializable {
        private Integer id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SizeDto implements Serializable {
        private Integer id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ColorDto implements Serializable {
        private Integer id;
    }
}
