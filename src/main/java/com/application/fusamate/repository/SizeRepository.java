package com.application.fusamate.repository;

import com.application.fusamate.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeRepository  extends JpaRepository<Size,Integer> {
    List<Size> findByNameLike(String name);

    @Query("select s from Size s inner join s.listProductDetail listProductDetail " +
            "where listProductDetail.product.id = ?1 and listProductDetail.color.id = ?2")
    List<Size> findByListProductDetail_Product_IdAndListProductDetail_Color_Id(Long id, Integer id1);
}
