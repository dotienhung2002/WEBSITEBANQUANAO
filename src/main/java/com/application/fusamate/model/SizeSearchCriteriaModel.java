package com.application.fusamate.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SizeSearchCriteriaModel extends FilterAndPagingModel {
    private String name;
}
