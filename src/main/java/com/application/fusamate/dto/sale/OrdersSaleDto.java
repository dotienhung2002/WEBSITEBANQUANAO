package com.application.fusamate.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class OrdersSaleDto implements Serializable {
    @NotBlank
    private final String name;
    @NotBlank
    private final String email;
    @NotBlank
    private final String phone;
    @NotBlank
    private final String address;
    @NotBlank
    private final String ward;
    @NotBlank
    private final String district;
    @NotBlank
    private final String province;
    @NotNull
    private final int shipType;
    @NotNull
    private final int paymentType;
    @NotNull
    private final double shipCost;
    private final String note;
    private final List<VoucherDto> listVoucher;
    @NotNull
    private final Long cartId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VoucherDto implements Serializable {
        private String code;
    }
}
