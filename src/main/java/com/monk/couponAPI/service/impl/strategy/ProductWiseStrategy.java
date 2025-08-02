package com.monk.couponAPI.service.impl.strategy;

import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.dto.CartItemDto;
import com.monk.couponAPI.entity.Coupon;
import com.monk.couponAPI.entity.ProductWiseCoupon;
import com.monk.couponAPI.enums.CouponType;
import com.monk.couponAPI.utils.DiscountCalculator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductWiseStrategy implements CouponStrategy{

    @Override
    public boolean supports(CouponType type) {
        return type == CouponType.PRODUCT_WISE;
    }

    @Override
    public boolean isApplicable(Coupon coupon, CartDto cart) {
        ProductWiseCoupon pwc = (ProductWiseCoupon) coupon;
        return cart.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(pwc.getProductId()));
    }

    @Override
    public BigDecimal calculateDiscount(Coupon coupon, CartDto cart) {
        ProductWiseCoupon productCoupon = (ProductWiseCoupon) coupon;
        Long targetProductId = productCoupon.getProductId();
        int discountPercentage = productCoupon.getDiscountPercentage();

        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CartItemDto item : cart.getItems()) {
            if (targetProductId.equals(item.getProductId())) {
                BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal itemDiscount = itemTotal.multiply(BigDecimal.valueOf(discountPercentage)).divide(BigDecimal.valueOf(100));

                totalDiscount = totalDiscount.add(itemDiscount);
            }
        }

        return totalDiscount;
    }

    @Override
    public CartDto applyCoupon(Coupon coupon, CartDto cart) {
        ProductWiseCoupon pwc = (ProductWiseCoupon) coupon;
        for (CartItemDto item : cart.getItems()) {
            if (item.getProductId().equals(pwc.getProductId())) {
                BigDecimal discount = DiscountCalculator.calculatePercentageDiscount(
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                        pwc.getDiscountPercentage()
                );
                // You could modify cart to reflect this discount
                item.setTotalDiscount(discount);
            }
        }
        return cart;
    }
}
