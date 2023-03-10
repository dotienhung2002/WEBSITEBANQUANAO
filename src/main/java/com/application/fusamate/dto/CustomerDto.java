package com.application.fusamate.dto;

import com.application.fusamate.configuration.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long id;

    private String password;

    private String email;

    private String name;
    @Pattern(regexp = Constants.REGEX_PHONE)
    private String phone;

    private int gender;

    @JsonFormat(pattern = "dd/MM/YYYY")
    @Temporal(TemporalType.DATE)
    private Date birthDay;

    private float height;
    private float weight;
    private int status = 1;

    @JsonFormat()
    private Date createdAt = new Date();
    @JsonFormat()
    private Date updatedAt = new Date();
    private String updatedBy;
}