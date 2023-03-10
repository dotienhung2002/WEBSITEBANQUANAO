package com.application.fusamate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateColorDto {
    private String name;
    private String description;
    private Date updatedAt;
    private String updatedBy;
}
