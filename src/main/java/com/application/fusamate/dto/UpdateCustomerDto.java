package com.application.fusamate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerDto {
    private String name;

    private int gender;

    // @JsonFormat(pattern="dd/MM/yyyy")
    // @Temporal(TemporalType.DATE)
    // private Date birthDay;

    private String phone;

    // private float height;

    // private float weight;

    private int status;
}