package com.application.fusamate.dto;

import com.application.fusamate.configuration.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    @Size(min = 3, max = 30)
    @NotBlank
    private String username;
    private String name;
    @NotNull
    private int gender;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date birthDay;
    @NotBlank
    @Pattern(regexp = Constants.REGEX_PHONE)
    private String phone;
    @NotBlank
    private String identityCard;
    private String note;
    @NotNull
    private int role;
    @NotNull
    private int status;
    @NotBlank
    @Email
    private String email;
    private String image;
    private String updatedBy;
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private Date updatedAt = new Date();

}



