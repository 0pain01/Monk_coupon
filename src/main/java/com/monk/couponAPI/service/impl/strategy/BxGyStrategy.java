package com.monk.couponAPI.service.impl.strategy;

import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.dto.CartItemDto;
import com.monk.couponAPI.entity.BxGyCoupon;
import com.monk.couponAPI.entity.BxProduct;
import com.monk.couponAPI.entity.Coupon;
import com.monk.couponAPI.entity.GyProduct;
import com.monk.couponAPI.enums.CouponType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BxGyStrategy implements CouponStrategy{
    @Override
    public boolean supports(CouponType type) {
        return type == CouponType.BXGY;
    }

    @Override
    public boolean isApplicable(Coupon coupon, CartDto cart) {
        BxGyCoupon bxgy = (BxGyCoupon) coupon;

        Map<Long, CartItemDto> cartMap = cart.getItems().stream()
                .collect(Collectors.toMap(CartItemDto::getProductId, item -> item));

        return calculateRepetitionCount(bxgy, cartMap) > 0;
    }

    @Override
    public BigDecimal calculateDiscount(Coupon coupon, CartDto cart) {
        BxGyCoupon bxgy = (BxGyCoupon) coupon;

        Map<Long, CartItemDto> cartMap = cart.getItems().stream()
                .collect(Collectors.toMap(CartItemDto::getProductId, item -> item));

        int repetition = calculateRepetitionCount(bxgy, cartMap);
        if (repetition == 0) return BigDecimal.ZERO;

        BigDecimal discount = BigDecimal.ZERO;

        for (GyProduct gp : bxgy.getGetProducts()) {
            CartItemDto item = cartMap.get(gp.getProductId());
            if (item != null) {
                int maxFreeQty = gp.getQuantityFree() * repetition;
                int actualEligibleQty = Math.min(item.getQuantity(), maxFreeQty);

                BigDecimal unitPrice = item.getPrice();
                BigDecimal itemDiscount = unitPrice.multiply(BigDecimal.valueOf(actualEligibleQty));

                discount = discount.add(itemDiscount);
            }
        }

        return discount;
    }

    @Override
    public CartDto applyCoupon(Coupon coupon, CartDto cart) {
        BxGyCoupon bxgy = (BxGyCoupon) coupon;

        Map<Long, CartItemDto> cartMap = cart.getItems().stream()
                .collect(Collectors.toMap(CartItemDto::getProductId, item -> item));

        int repetition = calculateRepetitionCount(bxgy, cartMap);
        if (repetition == 0) return cart;

        for (GyProduct gp : bxgy.getGetProducts()) {
            Long pid = gp.getProductId();
            int freeQty = gp.getQuantityFree() * repetition;

            CartItemDto item = cartMap.get(pid);
            if (item != null) {
                BigDecimal unitPrice = item.getPrice();
                int applicableFreeUnits = Math.min(item.getQuantity(), freeQty);

                BigDecimal currentDiscount = item.getTotalDiscount() != null ? item.getTotalDiscount() : BigDecimal.ZERO;
                BigDecimal additionalDiscount = unitPrice.multiply(BigDecimal.valueOf(applicableFreeUnits));

                item.setTotalDiscount(currentDiscount.add(additionalDiscount));
            }
        }

        return cart;
    }

    private int calculateRepetitionCount(BxGyCoupon coupon, Map<Long, CartItemDto> cartMap) {
        int maxRepeat = Integer.MAX_VALUE;

        for (BxProduct bp : coupon.getBuyProducts()) {
            CartItemDto item = cartMap.get(bp.getProductId());
            int availableQty = (item != null) ? item.getQuantity() : 0;

            int possibleRepeatsForThisProduct = availableQty / bp.getQuantityRequired();
            maxRepeat = Math.min(maxRepeat, possibleRepeatsForThisProduct);
        }

        return Math.max(maxRepeat, coupon.getRepetitionLimit());
    }
}
