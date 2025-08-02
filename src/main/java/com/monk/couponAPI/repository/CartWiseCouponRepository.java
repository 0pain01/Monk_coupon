package com.monk.couponAPI.repository;

import com.monk.couponAPI.entity.CartWiseCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartWiseCouponRepository extends JpaRepository<CartWiseCoupon,Long> {
}
