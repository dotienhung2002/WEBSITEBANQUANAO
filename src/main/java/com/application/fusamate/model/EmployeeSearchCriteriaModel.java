package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeSearchCriteriaModel extends FilterAndPagingModel {
    private String name;
    private String username;
    private String phone;
    private String identityCard;
    private String email;
    private Integer status;
}
