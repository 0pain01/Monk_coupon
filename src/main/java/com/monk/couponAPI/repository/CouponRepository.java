package com.monk.couponAPI.repository;

import com.monk.couponAPI.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
}
