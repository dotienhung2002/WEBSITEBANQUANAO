package com.application.fusamate.service.impl;

import com.application.fusamate.dto.VoucherDto;
import com.application.fusamate.entity.Voucher;
import com.application.fusamate.entity.stats.TopSalesCustomer;
import com.application.fusamate.model.VoucherSearchCriteriaModel;
import com.application.fusamate.repository.CustomerRepository;
import com.application.fusamate.repository.TopSalesCustomerRepository;
import com.application.fusamate.repository.VoucherRepository;
import com.application.fusamate.repository.criteria.VoucherCriteriaRepository;
import com.application.fusamate.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final TopSalesCustomerRepository topSalesCustomerRepository;
    private final CustomerRepository customerRepository;
    private final VoucherCriteriaRepository voucherCriteriaRepository;

    @Override
    public List<Voucher> createVoucherForLeadCustomers() {
        List<TopSalesCustomer> topSalesCustomers = topSalesCustomerRepository.findAll()
                .stream().filter(topSalesCustomer -> topSalesCustomer.getTotalPayment() >= 500_000)
                .collect(Collectors.toList());
        List<Voucher> vouchersForLeadCustomers = new ArrayList<>();
        topSalesCustomers.forEach(topSalesCustomer -> {
            Voucher voucher = new Voucher();
            int index = 0;
            if (topSalesCustomer.getTotalPayment() >= 10_000_000) {
                if (voucherRepository.findByCodeLikeIgnoreCaseAndCustomer_EmailLikeIgnoreCase("1MFORLOVE",
                        topSalesCustomer.getEmail()).isEmpty()) {
                    voucher.setName("Voucher cho khách hàng có tổng doanh thu >= 10 triệu");
                    voucher.setCode("1MFORLOVE");
                    voucher.setMoney(1_000_000.0);
                    index++;
                }
            } else if (topSalesCustomer.getTotalPayment() >= 5_000_000) {
                if (voucherRepository.findByCodeLikeIgnoreCaseAndCustomer_EmailLikeIgnoreCase("500KFORLOVE",
                        topSalesCustomer.getEmail()).isEmpty()) {
                    voucher.setName("Voucher cho khách hàng có tổng doanh thu >= 5 triệu");
                    voucher.setCode("500KFORLOVE");
                    voucher.setMoney(500_000.0);
                    index++;
                }
            } else if (topSalesCustomer.getTotalPayment() >= 1_000_000) {
                if (voucherRepository.findByCodeLikeIgnoreCaseAndCustomer_EmailLikeIgnoreCase("100KFORLOVE",
                        topSalesCustomer.getEmail()).isEmpty()) {
                    voucher.setName("Voucher cho khách hàng có tổng doanh thu >= 1 triệu");
                    voucher.setCode("100KFORLOVE");
                    voucher.setMoney(100_000.0);
                    index++;
                }
            } else if (topSalesCustomer.getTotalPayment() >= 500_000) {
                if (voucherRepository.findByCodeLikeIgnoreCaseAndCustomer_EmailLikeIgnoreCase("50KFORLOVE",
                        topSalesCustomer.getEmail()).isEmpty()) {
                    voucher.setName("Voucher cho khách hàng có tổng doanh thu >= 500K");
                    voucher.setCode("50KFORLOVE");
                    voucher.setMoney(50_000.0);
                    index++;
                }
            }

            if (index != 0) {
                voucher.setSlot(1);
                voucher.setStartDate(new Date());
                voucher.setEndDate(Date.from(new Date().toInstant().plus(90, ChronoUnit.DAYS)));
                voucher.setActive(1);
                voucher.setCreatedAt(new Date());
                voucher.setUpdatedAt(new Date());
                voucher.setUpdatedBy("system");
                voucher.setCustomer(customerRepository.findByEmailLikeIgnoreCase(topSalesCustomer.getEmail())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng")));
                voucherRepository.save(voucher);
                vouchersForLeadCustomers.add(voucher);
            }
        });
        return vouchersForLeadCustomers;
    }

    @Override
    public List<Voucher> getUsableVouchersByEmail(String email) {
        return voucherRepository.findByCustomer_EmailLikeIgnoreCase(email)
                .stream().filter(voucher -> voucher.getSlot() > 0)
                .filter(voucher -> (voucher.getStartDate().before(new Date()) || voucher.getStartDate().equals(new Date())) &&
                        (voucher.getEndDate().equals(new Date()) || voucher.getEndDate().after(new Date())))
                .filter(voucher -> voucher.getActive() == 1)
                .collect(Collectors.toList());
    }

    @Override
    public List<Voucher> getUsableVoucherForAnonymous() {
        return voucherRepository.findAll()
                .stream().filter(voucher -> voucher.getCustomer() == null)
                .filter(voucher -> voucher.getSlot() > 0)
                .filter(voucher -> (voucher.getStartDate().before(new Date()) || voucher.getStartDate().equals(new Date())) &&
                        (voucher.getEndDate().equals(new Date()) || voucher.getEndDate().after(new Date())))
                .filter(voucher -> voucher.getActive() == 1)
                .collect(Collectors.toList());
    }

    @Override
    public Voucher createVoucher(VoucherDto voucherDto) {
        if (voucherDto.getEndDate().before(voucherDto.getStartDate()) || voucherDto.getEndDate().before(new Date()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc không hợp lệ");
        Voucher voucher = new Voucher();
        voucher.setName(voucherDto.getName().trim());
        voucher.setCode(voucherDto.getCode().trim().toUpperCase());
        voucher.setMoney(voucherDto.getMoney());
        voucher.setSlot(voucherDto.getSlot());
        voucher.setStartDate(voucherDto.getStartDate());
        voucher.setEndDate(voucherDto.getEndDate());
        voucher.setActive(voucherDto.getActive());
        voucher.setCreatedAt(new Date());
        voucher.setUpdatedAt(new Date());
        voucher.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        if (voucherDto.getEmail() != null) {
            if (!voucherRepository.findByCodeLikeIgnoreCaseAndCustomer_EmailLikeIgnoreCase(voucherDto.getCode(), voucherDto.getEmail()).isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Người dùng đã có hoặc đã sử dụng mã voucher này");
            voucher.setCustomer(customerRepository.findByEmailLikeIgnoreCase(voucherDto.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng")));
        }
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher updateVoucher(Long id, VoucherDto voucherDto) {
        if (voucherDto.getEndDate().before(voucherDto.getStartDate()) || voucherDto.getEndDate().before(new Date()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc không hợp lệ");

        Voucher updateVoucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy voucher"));

        if (voucherDto.getEmail() != null) {
            if (!voucherRepository.findByCodeLikeIgnoreCaseAndCustomer_EmailLikeIgnoreCase(voucherDto.getCode(), voucherDto.getEmail()).isEmpty()
                    && (!voucherDto.getEmail().trim().equalsIgnoreCase(updateVoucher.getCustomer().getEmail().trim())
                    || !voucherDto.getCode().trim().equalsIgnoreCase(updateVoucher.getCode().trim())))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Người dùng đã có hoặc đã sử dụng mã voucher này");
        }

        updateVoucher.setName(voucherDto.getName().trim());
        updateVoucher.setCode(voucherDto.getCode().trim().toUpperCase());
        updateVoucher.setMoney(voucherDto.getMoney());
        updateVoucher.setSlot(voucherDto.getSlot());
        updateVoucher.setStartDate(voucherDto.getStartDate());
        updateVoucher.setEndDate(voucherDto.getEndDate());
        updateVoucher.setActive(voucherDto.getActive());
        updateVoucher.setNote(voucherDto.getNote());
        updateVoucher.setUpdatedAt(new Date());
        updateVoucher.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        return voucherRepository.save(updateVoucher);
    }

    @Override
    public Page<Voucher> getVouchers(VoucherSearchCriteriaModel voucherSearchCriteriaModel) {
        return voucherCriteriaRepository.findAllWithFilters(voucherSearchCriteriaModel);
    }

    @Override
    public Voucher getVoucherDetail(Long id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy voucher"));
    }
}
