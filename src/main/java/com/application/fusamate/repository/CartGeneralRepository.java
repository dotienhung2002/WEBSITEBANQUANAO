package com.application.fusamate.repository;

import com.application.fusamate.entity.CartGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import lombok.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CartGeneralRepository extends JpaRepository<CartGeneral, Long> {
    @Query("select c from CartGeneral c where c.userAuthToken = ?1")
    Optional<CartGeneral> findByUserAuthToken(@NonNull String userAuthToken);

    @Transactional
    @Modifying
    @Query("delete from CartGeneral c where c.userAuthToken = ?1")
    void deleteByUserAuthToken(@NonNull String userAuthToken);
}