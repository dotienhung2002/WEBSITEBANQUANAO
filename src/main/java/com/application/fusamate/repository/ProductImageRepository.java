package com.application.fusamate.repository;

import com.application.fusamate.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("select p from ProductImage p where p.product.id = ?1 and p.color.id = ?2")
    List<ProductImage> findByProduct_IdAndColor_Id(@NonNull Long productId, @NonNull Integer colorId);
}