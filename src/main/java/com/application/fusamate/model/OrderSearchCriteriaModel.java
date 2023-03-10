package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderSearchCriteriaModel extends FilterAndPagingModel{

    private Integer status;
    private Integer paymentStatus;

    private String name;

    private String phone;

    private String email;

    private Date createdAt;

    private Date updatedAt;
}
