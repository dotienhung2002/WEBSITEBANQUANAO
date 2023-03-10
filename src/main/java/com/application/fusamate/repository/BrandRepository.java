package com.application.fusamate.repository;

import com.application.fusamate.entity.Brand;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    List<Brand> findByNameLike(@NonNull String name);

    List<Brand> findByStatus(@NonNull int status);

    long countByStatus(@NonNull int status);

}
