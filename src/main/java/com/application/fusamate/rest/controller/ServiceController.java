package com.application.fusamate.rest.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.criteria.Order;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.application.fusamate.dto.UpdateOrdersDto;
import com.application.fusamate.entity.Orders;
import com.application.fusamate.model.JwtResponseModel;
import com.application.fusamate.model.PaymentModel;
import com.application.fusamate.model.ResponseChangeDataModel;
import com.application.fusamate.service.sale.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Validated
public class ServiceController {
    // ipAdress = req.getHeader("X-FORWARDED-FOR");
    // if ?(ipAdress == null) {
    // ? ipAdress = req.getRemoteAddr();
    // }?
    private final OrderService orderService;

    @PostMapping("/public/payment/vnpay")
    public ResponseEntity<?> paymentVnpay(@RequestBody @Valid PaymentModel body, HttpServletRequest req)
            throws Exception {
        Orders order = orderService.getOrdersById((body.getOrderId()));
        req.getHeader("X-FORWARDED-FOR");
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String returnUrl = "http://fusamate.site/cart/checkout-success";
        String vnp_TxnRef = body.getOrderId().toString();
        String vnp_IpAddr = req.getRemoteAddr();
        String vnp_TmnCode = "GYMYTYLT";
        int amount = ((int) order.getTotalPayment()) * 100; // phai lay tu total payment backend
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_OrderInfo", body.getVnp_OrderInfo());

        // String bank_code = body.getBankcode();
        // if (bank_code != null && !bank_code.isEmpty()) {
        // vnp_Params.put("vnp_BankCode", bank_code);
        // }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // noi dung thanh toan
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");

        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        // Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        // Billing
        vnp_Params.put("vnp_Bill_Mobile", order.getPhone());
        vnp_Params.put("vnp_Bill_Email", order.getEmail());

        vnp_Params.put("vnp_Bill_Address", order.getAddress());

        System.out.println(vnp_Params);
        // Build data to hash and querystring
        List fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = ServiceController.hmacSHA512("KYBDKGQDNGBJXZLECPNEXJCMCPPKVDUC", hashData.toString());

        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html" + "?" + queryUrl;
        Map<String, String> job = new HashMap<>();
        job.put("data", paymentUrl);
        return ResponseEntity.ok(job);
    }

    // http://fusamate.site/?vnp_Amount=35108000&vnp_BankCode=NCB&vnp_BankTranNo=VNP13897988&vnp_CardType=ATM&vnp_OrderInfo=hihii22&vnp_PayDate=20221208012350&vnp_ResponseCode=00&vnp_TmnCode=GYMYTYLT&vnp_TransactionNo=13897988&vnp_TransactionStatus=00&vnp_TxnRef=34&vnp_SecureHash=9b78ceff41c9d5a0b2553ca6d38e5b65ec046da42404fc4f691aea6f61d0ef3e95b80fe09c12f749c6e04746f334354de8772b93c7f7c81a5338fe7ba1a49d8d

    @GetMapping("/public/payment/vnpay_ipn")
    public ResponseEntity<?> paymentVnpayIpn(HttpServletRequest req) throws Exception {
        Orders order = null;
        try {
            Long orderId = Long.parseLong(req.getParameter("vnp_TxnRef"));
            order = this.orderService.getOrdersById(orderId);
            Map fields = new HashMap();
            for (Enumeration params = req.getParameterNames(); params.hasMoreElements();) {
                String fieldName = URLEncoder.encode((String) params.nextElement(),
                        StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(req.getParameter(fieldName),
                        StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = req.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            String signValue = this.hashAllFields(fields);
            if (signValue.equals(vnp_SecureHash)) {

                boolean checkOrderId = true; // vnp_TxnRef exists in your database
                boolean checkAmount = true; // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the
                boolean checkOrderStatus = true; // PaymnentStatus = 0 (pending)

                if (checkOrderId) {

                    if (checkAmount) {
                        if (checkOrderStatus) {
                            if ("00".equals(req.getParameter("vnp_ResponseCode"))) {
                                // Here Code update PaymnentStatus = 1 into your Database
System.out.println("thanh cong ne");

                                UpdateOrdersDto updateOrdersDto = new UpdateOrdersDto(order.getName(), order.getPhone(),
                                        order.getAddress(), order.getNote(), 0, true);

                                this.orderService.updateOrdersById(updateOrdersDto, order.getId());

                                return ResponseEntity.ok().body(
                                    new ResponseChangeDataModel(this.orderService.getOrdersById(order.getId()),
                                            HttpStatus.OK.value()));
                            } else {

                                // Here Code update PaymnentStatus = 2 into your Database
                                return ResponseEntity.internalServerError().body(
                                    new ResponseChangeDataModel(null,
                                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
                            }

                            // return ResponseEntity.ok().body(,HttpStatus.OK.value());
                           


                        } else {

                            return ResponseEntity.ok().body(
                                    new ResponseChangeDataModel(this.orderService.getOrdersById(order.getId()),
                                            HttpStatus.OK.value()));

                        }
                    } else {
                        System.out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                        UpdateOrdersDto updateOrdersDto = new UpdateOrdersDto(order.getName(), order.getPhone(),
                                order.getAddress(), order.getNote(), 5, false);

                        this.orderService.cancelOrdersById(updateOrdersDto, order.getId());
                        return ResponseEntity.internalServerError().body(HttpStatus.BAD_REQUEST.value());

                    }
                } else {
                    System.out.print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
                    UpdateOrdersDto updateOrdersDto = new UpdateOrdersDto(order.getName(), order.getPhone(),
                            order.getAddress(), order.getNote(), 5, false);

                    this.orderService.cancelOrdersById(updateOrdersDto, order.getId());
                    return ResponseEntity.internalServerError().body(HttpStatus.NOT_FOUND.value());

                }
            } else {
                System.out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
                UpdateOrdersDto updateOrdersDto = new UpdateOrdersDto(order.getName(), order.getPhone(),
                        order.getAddress(), order.getNote(), 5, false);

                this.orderService.cancelOrdersById(updateOrdersDto, order.getId());
                return ResponseEntity.internalServerError().body(HttpStatus.OK.value());

            }
        } catch (Exception e) {
            System.out.print("{\"RspCode\":\"99\",\"Message\":\"Unknow error\"}");
            return ResponseEntity.internalServerError().body(HttpStatus.INTERNAL_SERVER_ERROR.value());

        }

    }

    @GetMapping("/public/payment/vnpay_return")
    public ResponseEntity<?> paymentVnpayReturn(@RequestBody PaymentModel body, HttpServletRequest req)
            throws Exception {
        Map fields = new HashMap();
        for (Enumeration params = req.getParameterNames(); params.hasMoreElements();) {
            String fieldName = (String) params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = req.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = this.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(req.getParameter("vnp_ResponseCode"))) {
                System.out.print("GD Thanh cong");
            } else {
                System.out.print("GD Khong thanh cong");
            }

        } else {
            System.out.print("Chu ky khong hop le");
        }
        return null;
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("hmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "hmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public static String hashAllFields(Map fields) {
        // create a list and sort it
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        // create a buffer for the md5 input and add the secure secret first
        StringBuilder sb = new StringBuilder();

        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512("KYBDKGQDNGBJXZLECPNEXJCMCPPKVDUC", sb.toString());
    }

}
