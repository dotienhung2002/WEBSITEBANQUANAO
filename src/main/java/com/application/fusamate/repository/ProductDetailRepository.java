package com.application.fusamate.repository;

import com.application.fusamate.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {

    @Query(value = "select sum(pd.avail_amount) " +
            "from product_detail pd join product p on p.id = pd.product_id where p.id = ?1", nativeQuery = true)
    Integer sumAvailAmountByProductId(Long productId);

    @Query("select p from ProductDetail p where p.product.id = ?1 and p.color.id = ?2 and p.size.id = ?3")
    ProductDetail findByProduct_IdAndColor_IdAndSize_Id(@NonNull Long productId,
                                                        @NonNull Integer colorId, @NonNull Integer sizeId);

}
