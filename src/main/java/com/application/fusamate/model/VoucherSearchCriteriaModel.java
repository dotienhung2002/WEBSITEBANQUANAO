package com.application.fusamate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoucherSearchCriteriaModel extends FilterAndPagingModel{
    private Customer customer;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    private Integer moneyRange;
    private Integer active;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Customer implements Serializable {
        private String email;
    }
}
