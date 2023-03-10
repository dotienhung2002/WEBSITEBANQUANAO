package com.application.fusamate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDto implements Serializable {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    @Positive
    private Double money;
    @NonNull
    @Positive
    private Integer slot;
    @NonNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @NonNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @NonNull
    private Integer active;
    private String note;
    @Email
    private String email;
}
