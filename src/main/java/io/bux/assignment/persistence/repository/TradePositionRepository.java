package io.bux.assignment.persistence.repository;

import io.bux.assignment.persistence.entity.TradePosition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradePositionRepository extends CrudRepository<TradePosition, Long> {

    TradePosition findByProductId(String productId);
}
