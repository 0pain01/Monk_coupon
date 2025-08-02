package com.monk.couponAPI.repository;

import com.monk.couponAPI.entity.GyProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GyProductRepository extends JpaRepository<GyProduct,Long> {
}
