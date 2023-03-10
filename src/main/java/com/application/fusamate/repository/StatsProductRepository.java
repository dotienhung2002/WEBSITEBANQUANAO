package com.application.fusamate.repository;

import com.application.fusamate.entity.StatsProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsProductRepository extends JpaRepository<StatsProduct, Long> {
    List<StatsProduct> findByOrderDetail_Id(@NonNull Long id);
}