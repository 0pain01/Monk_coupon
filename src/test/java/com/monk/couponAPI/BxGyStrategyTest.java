package com.monk.couponAPI;

import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.dto.CartItemDto;
import com.monk.couponAPI.entity.BxGyCoupon;
import com.monk.couponAPI.entity.BxProduct;
import com.monk.couponAPI.entity.GyProduct;
import com.monk.couponAPI.service.impl.strategy.BxGyStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BxGyStrategyTest {
    private final BxGyStrategy strategy = new BxGyStrategy();

    @Test
    void testCalculateDiscount_returnsCorrectDiscount() {
        BxGyCoupon coupon = new BxGyCoupon();
        coupon.setRepetitionLimit(2);
        coupon.setBuyProducts(List.of(
                new BxProduct(null, 1L, 3, coupon),
                new BxProduct(null, 2L, 3, coupon)
        ));
        coupon.setGetProducts(List.of(new GyProduct(null, 3L, 1, coupon)));

        CartDto cart = new CartDto(List.of(
                new CartItemDto(1L, 6, new BigDecimal("50.00")),
                new CartItemDto(2L, 3, new BigDecimal("30.00")),
                new CartItemDto(3L, 2, new BigDecimal("25.00"))
        ));

        BigDecimal discount = strategy.calculateDiscount(coupon, cart);
        assertEquals(new BigDecimal("50.00"), discount.setScale(2));
    }

    @Test
    void testApplyCoupon_setsCorrectDiscount() {
        BxGyCoupon coupon = new BxGyCoupon();
        coupon.setRepetitionLimit(1);
        coupon.setBuyProducts(List.of(
                new BxProduct(null, 1L, 3, coupon)
        ));
        coupon.setGetProducts(List.of(new GyProduct(null, 3L, 1, coupon)));

        CartItemDto item3 = new CartItemDto(3L, 1, new BigDecimal("25.00"));
        CartDto cart = new CartDto(List.of(
                new CartItemDto(1L, 3, new BigDecimal("50.00")),
                item3
        ));

        strategy.applyCoupon(coupon, cart);

        assertEquals(new BigDecimal("25.00"), item3.getTotalDiscount());
    }
}
