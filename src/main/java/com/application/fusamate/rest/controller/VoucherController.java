package com.application.fusamate.rest.controller;

import com.application.fusamate.dto.VoucherDto;
import com.application.fusamate.entity.Voucher;
import com.application.fusamate.model.ResponseChangeDataModel;
import com.application.fusamate.model.ResponseGetDataModel;
import com.application.fusamate.model.VoucherSearchCriteriaModel;
import com.application.fusamate.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping("/voucher/create-voucher-for-lead-customers")
    public ResponseEntity<?> createVoucherForLeadCustomers() {
        Map<Object, Object> map = new HashMap<>();
        map.put("data", voucherService.createVoucherForLeadCustomers());
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/public/voucher/get-usable")
    public ResponseEntity<?> getUsableVouchersByEmail(@RequestParam String email) {
        Map<Object, Object> map = new HashMap<>();
        map.put("data", voucherService.getUsableVouchersByEmail(email));
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/public/voucher/get-usable-for-anonymous")
    public ResponseEntity<?> getUsableVouchersForAnonymous() {
        Map<Object, Object> map = new HashMap<>();
        map.put("data", voucherService.getUsableVoucherForAnonymous());
        return ResponseEntity.ok().body(map);
    }

    @PostMapping("/voucher/create")
    public ResponseEntity<?> createVoucher(@RequestBody @Valid VoucherDto voucherDto) {
        return ResponseEntity.ok().body(new ResponseChangeDataModel(
                voucherService.createVoucher(voucherDto), HttpStatus.OK.value()));
    }

    @GetMapping("/voucher/{id}")
    public ResponseEntity<?> getVoucherDetail(@PathVariable Long id) {
        return ResponseEntity.ok().body(voucherService.getVoucherDetail(id));
    }

    @PutMapping("/voucher/update/{id}")
    public ResponseEntity<?> updateVoucher(@PathVariable Long id, @RequestBody @Valid VoucherDto voucherDto) {
        Voucher updateVoucher = voucherService.updateVoucher(id, voucherDto);
        this.createVoucherForLeadCustomers();
        return ResponseEntity.ok().body(new ResponseChangeDataModel(updateVoucher, HttpStatus.OK.value()));
    }

    @PostMapping("/voucher/get-list")
    public ResponseEntity<?> getVouchers(@RequestBody @Valid VoucherSearchCriteriaModel voucherSearchCriteriaModel) {
        Page<Voucher> voucherPage = voucherService.getVouchers(voucherSearchCriteriaModel);
        return ResponseEntity.ok().body(new ResponseGetDataModel(voucherPage.getContent(), voucherPage.getNumber(),
                voucherPage.getTotalElements(), voucherPage.getTotalPages()));
    }

}
