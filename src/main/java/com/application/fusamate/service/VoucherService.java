package com.application.fusamate.service;

import com.application.fusamate.dto.VoucherDto;
import com.application.fusamate.entity.Voucher;
import com.application.fusamate.model.VoucherSearchCriteriaModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VoucherService {

    List<Voucher> createVoucherForLeadCustomers();

    List<Voucher> getUsableVouchersByEmail(String email);

    List<Voucher> getUsableVoucherForAnonymous();

    Voucher createVoucher(VoucherDto voucherDto);

    Voucher updateVoucher(Long id, VoucherDto voucherDto);

    Page<Voucher> getVouchers(VoucherSearchCriteriaModel voucherSearchCriteriaModel);

    Voucher getVoucherDetail(Long id);

}
