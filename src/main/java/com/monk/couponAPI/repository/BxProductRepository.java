package com.monk.couponAPI.repository;

import com.monk.couponAPI.entity.BxProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BxProductRepository extends JpaRepository<BxProduct,Long> {
}
