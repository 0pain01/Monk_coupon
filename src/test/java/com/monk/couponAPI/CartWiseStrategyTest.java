package com.monk.couponAPI;

import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.dto.CartItemDto;
import com.monk.couponAPI.entity.CartWiseCoupon;
import com.monk.couponAPI.service.impl.strategy.CartWiseStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

public class CartWiseStrategyTest {
    private final CartWiseStrategy strategy = new CartWiseStrategy();

    @Test
    void testCalculateDiscount_whenThresholdIsMet() {
        CartWiseCoupon coupon = new CartWiseCoupon();
        coupon.setThreshold(new BigDecimal("100"));
        coupon.setDiscountPercentage(10);

        CartDto cart = new CartDto(List.of(
                new CartItemDto(1L, 2, new BigDecimal("60")), // total: 120
                new CartItemDto(2L, 1, new BigDecimal("30"))  // total: 30
        ));

        BigDecimal discount = strategy.calculateDiscount(coupon, cart);

        // Total = 150 → 10% of 150 = 15.00
        assertEquals(new BigDecimal("15.00"), discount.setScale(2));
    }

    @Test
    void testCalculateDiscount_whenThresholdNotMet_returnsZero() {
        CartWiseCoupon coupon = new CartWiseCoupon();
        coupon.setThreshold(new BigDecimal("200"));
        coupon.setDiscountPercentage(15);

        CartDto cart = new CartDto(List.of(
                new CartItemDto(1L, 2, new BigDecimal("50")) // total: 100
        ));

        BigDecimal discount = strategy.calculateDiscount(coupon, cart);
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void testApplyCoupon_distributesDiscountProportionally() {
        CartWiseCoupon coupon = new CartWiseCoupon();
        coupon.setThreshold(new BigDecimal("100"));
        coupon.setDiscountPercentage(10);

        CartItemDto item1 = new CartItemDto(1L, 2, new BigDecimal("50"));  // total: 100
        CartItemDto item2 = new CartItemDto(2L, 1, new BigDecimal("50"));  // total: 50

        CartDto cart = new CartDto(List.of(item1, item2));
        strategy.applyCoupon(coupon, cart);

        // Total = 150, discount = 15 → item1 gets 10, item2 gets 5
        assertEquals(new BigDecimal("10.00"), item1.getTotalDiscount());
        assertEquals(new BigDecimal("5.00"), item2.getTotalDiscount());
    }

    @Test
    void testApplyCoupon_whenBelowThreshold_doesNothing() {
        CartWiseCoupon coupon = new CartWiseCoupon();
        coupon.setThreshold(new BigDecimal("200"));
        coupon.setDiscountPercentage(10);

        CartItemDto item = new CartItemDto(1L, 1, new BigDecimal("100"));
        CartDto cart = new CartDto(List.of(item));

        strategy.applyCoupon(coupon, cart);

        assertEquals(BigDecimal.ZERO,item.getTotalDiscount());
    }
}
