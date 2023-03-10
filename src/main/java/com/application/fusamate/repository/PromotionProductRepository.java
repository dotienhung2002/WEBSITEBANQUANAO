package com.application.fusamate.repository;

import com.application.fusamate.entity.PromotionProduct;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {
    PromotionProduct findByName(String name);

    Optional<PromotionProduct> findById(Long id);

    @Query("select p from PromotionProduct p where p.status = ?1 and p.product.id = ?2" +
            " order by p.updatedAt desc")
    List<PromotionProduct> findByStatusAndProduct_Id(@NonNull boolean status, @NonNull Long id);

}
