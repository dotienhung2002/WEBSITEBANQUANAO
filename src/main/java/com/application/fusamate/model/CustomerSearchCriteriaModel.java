package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerSearchCriteriaModel  extends FilterAndPagingModel{
    private String name;
    private String phone;
    private String email;
    private Integer status;
}
