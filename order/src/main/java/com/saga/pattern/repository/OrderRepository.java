package com.saga.pattern.repository;

import com.saga.pattern.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByTransactionId(String transactionId);

    @Transactional(propagation = Propagation.MANDATORY)
    void deleteByTransactionId(String transactionId);
}
