package com.application.fusamate.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductStatusDto {

    private Long active;

    private Long inactive;

    private Long outOfStock;

    private CategoryStatusDto categoryStats;

    private BrandStatusDto brandStats;

    private Long allMadeIns;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CategoryStatusDto {
        private Long active;
        private Long inactive;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class BrandStatusDto {
        private Long active;
        private Long inactive;
    }

}
