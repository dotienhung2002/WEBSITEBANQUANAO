package com.application.fusamate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
public class CategoryDto implements Serializable {
    private final String name;
    private final int status;
    private final int productSetId;
}
