package com.monk.couponAPI.service;

import com.monk.couponAPI.dto.ApplicableCouponResponse;
import com.monk.couponAPI.dto.ApplyCouponResponse;
import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.dto.CouponDto;
import com.monk.couponAPI.entity.Coupon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CouponService {
    Coupon createCoupon(CouponDto dto);
    List<Coupon> getAllCoupons();
    Coupon getCouponById(Long id);
    Coupon updateCoupon(Long id, CouponDto dto);
    String deleteCoupon(Long id);
    ApplicableCouponResponse getApplicableCoupons(CartDto cart);
    ApplyCouponResponse applyCoupon(Long couponId, CartDto cart);
}
