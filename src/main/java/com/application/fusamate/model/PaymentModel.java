package com.application.fusamate.model;
import com.application.fusamate.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentModel implements Serializable{
    private Long orderId;
    // private String bankcode;
    private String vnp_OrderInfo;
    
//     vnp_BankCode=VNPAYQRThanh toán quét mã QR
// vnp_BankCode=VNBANKThẻ ATM - Tài khoản ngân hàng nội địa
// vnp_BankCode=INTCARDThẻ thanh toán quốc tế
//lay o frontend
}
