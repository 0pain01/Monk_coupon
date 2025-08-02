package com.monk.couponAPI.service.impl.strategy;

import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.dto.CartItemDto;
import com.monk.couponAPI.entity.CartWiseCoupon;
import com.monk.couponAPI.entity.Coupon;
import com.monk.couponAPI.enums.CouponType;
import com.monk.couponAPI.utils.DiscountCalculator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CartWiseStrategy implements CouponStrategy{
    @Override
    public boolean supports(CouponType type) {
        return type == CouponType.CART_WISE;
    }

    @Override
    public boolean isApplicable(Coupon coupon, CartDto cart) {
        CartWiseCoupon c = (CartWiseCoupon) coupon;
        BigDecimal total = DiscountCalculator.sumCartTotal(cart.getItems());
        return total.compareTo(c.getThreshold()) > 0;
    }

    @Override
    public BigDecimal calculateDiscount(Coupon coupon, CartDto cart) {
        if (!isApplicable(coupon, cart)) return BigDecimal.ZERO;

        BigDecimal total = DiscountCalculator.sumCartTotal(cart.getItems());
        CartWiseCoupon c = (CartWiseCoupon) coupon;
        return DiscountCalculator.calculatePercentageDiscount(total, c.getDiscountPercentage());
    }

    @Override
    public CartDto applyCoupon(Coupon coupon, CartDto cart) {
        CartWiseCoupon cartCoupon = (CartWiseCoupon) coupon;

        // Step 1: Calculate total cart price
        BigDecimal totalCartValue = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Step 2: Check threshold condition
        if (totalCartValue.compareTo(cartCoupon.getThreshold()) < 0) {
            return cart; // Not applicable
        }

        // Step 3: Calculate total discount
        BigDecimal totalDiscount = DiscountCalculator.calculatePercentageDiscount(
                totalCartValue, cartCoupon.getDiscountPercentage()
        );

        // Step 4: Distribute discount proportionally to items
        for (CartItemDto item : cart.getItems()) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal itemShare = itemTotal.divide(totalCartValue, 4, RoundingMode.HALF_UP);
            BigDecimal itemDiscount = totalDiscount.multiply(itemShare).setScale(2, RoundingMode.HALF_UP);
            item.setTotalDiscount(itemDiscount);
        }

        return cart;
    }
}
