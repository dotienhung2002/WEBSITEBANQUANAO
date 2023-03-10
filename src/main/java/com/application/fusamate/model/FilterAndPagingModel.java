package com.application.fusamate.model;

import com.application.fusamate.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class FilterAndPagingModel {
    private  int pageNumber = Constants.DEFAULT_PAGE;
    private  int pageSize = Constants.DEFAULT_PAGE_SIZE;
    private  Sort.Direction sortDirection = Sort.Direction.ASC;
    private String sortBy = "id";
}
