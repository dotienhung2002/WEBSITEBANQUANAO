package com.application.fusamate.repository;

import com.application.fusamate.entity.Category;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    long countByStatus(@NonNull int status);

    List<Category> findByStatus(@NonNull int status);

    List<Category> findByProductSet_IdAndNameLike(@NonNull Integer id, @NonNull String name);
}
