package com.monk.couponAPI.service.impl.strategy;

import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.entity.Coupon;
import com.monk.couponAPI.enums.CouponType;

import java.math.BigDecimal;

public interface CouponStrategy {
    boolean supports(CouponType type);

    boolean isApplicable(Coupon coupon, CartDto cart);

    BigDecimal calculateDiscount(Coupon coupon, CartDto cart);

    CartDto applyCoupon(Coupon coupon, CartDto cart);
}
