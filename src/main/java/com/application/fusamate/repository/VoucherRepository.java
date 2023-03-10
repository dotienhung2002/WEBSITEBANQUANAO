package com.application.fusamate.repository;

import com.application.fusamate.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository  extends JpaRepository<Voucher,Long> {
    List<Voucher> findByCustomer_EmailLikeIgnoreCase(@NonNull String email);

    @Query("select v from Voucher v where upper(v.code) like upper(?1) and upper(v.customer.email) like upper(?2)")
    List<Voucher> findByCodeLikeIgnoreCaseAndCustomer_EmailLikeIgnoreCase(@NonNull String code, @NonNull String email);

    Voucher findByCodeLikeIgnoreCase(@NonNull String code);
}
