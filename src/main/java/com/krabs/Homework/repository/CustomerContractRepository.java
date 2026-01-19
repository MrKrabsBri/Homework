package com.krabs.Homework.repository;

import com.krabs.Homework.entity.OrderDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerContractRepository extends JpaRepository<OrderDocumentEntity, Long> {
    Optional<OrderDocumentEntity> findByServiceId(String serviceId);
}
