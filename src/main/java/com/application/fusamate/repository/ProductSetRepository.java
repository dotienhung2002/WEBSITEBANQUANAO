package com.application.fusamate.repository;

import com.application.fusamate.entity.ProductSet;
import org.springframework.data.jpa.repository.JpaRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSetRepository extends JpaRepository<ProductSet, Integer> {
    List<ProductSet> findByName(@NonNull String name);

}
