package com.monk.couponAPI.repository;

import com.monk.couponAPI.entity.ProductWiseCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductWiseCouponRepository extends JpaRepository<ProductWiseCoupon,Long> {
}
