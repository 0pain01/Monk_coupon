package com.monk.couponAPI;

import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.dto.CartItemDto;
import com.monk.couponAPI.entity.ProductWiseCoupon;
import com.monk.couponAPI.service.impl.strategy.ProductWiseStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProductWiseStrategyTest {
    private final ProductWiseStrategy strategy = new ProductWiseStrategy();

    @Test
    void testCalculateDiscount_appliesCorrectly() {
        ProductWiseCoupon coupon = new ProductWiseCoupon();
        coupon.setProductId(1L);
        coupon.setDiscountPercentage(20);

        CartItemDto item = new CartItemDto(1L, 2, new BigDecimal("100"));
        CartDto cart = new CartDto(List.of(item));

        BigDecimal discount = strategy.calculateDiscount(coupon, cart);
        assertEquals(new BigDecimal("40.00"), discount.setScale(2));
    }

    @Test
    void testApplyCoupon_setsTotalDiscount() {
        ProductWiseCoupon coupon = new ProductWiseCoupon();
        coupon.setProductId(1L);
        coupon.setDiscountPercentage(50);

        CartItemDto item = new CartItemDto(1L, 2, new BigDecimal("20"));
        CartDto cart = new CartDto(List.of(item));

        strategy.applyCoupon(coupon, cart);

        assertEquals(new BigDecimal("20.00"), item.getTotalDiscount());
    }
}
