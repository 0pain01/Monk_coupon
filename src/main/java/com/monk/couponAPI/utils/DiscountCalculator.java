package com.monk.couponAPI.utils;

import com.monk.couponAPI.dto.CartItemDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscountCalculator {
    public static BigDecimal calculatePercentageDiscount(BigDecimal amount, int percent) {
        return amount.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal sumCartTotal(List<CartItemDto> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Map<Long, BigDecimal> distributeCartDiscount(
            BigDecimal totalDiscount,
            List<CartItemDto> items
    ) {
        BigDecimal total = sumCartTotal(items);
        Map<Long, BigDecimal> distribution = new HashMap<>();

        items.forEach(item -> {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal ratio = itemTotal.divide(total, 4, RoundingMode.HALF_UP);
            distribution.put(item.getProductId(), totalDiscount.multiply(ratio));
        });

        return distribution;
    }
}
