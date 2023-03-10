package com.application.fusamate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseGetDataModel{
    private Object data;
    private int currentPage;
    private long countItems;
    private int countPages;
}
