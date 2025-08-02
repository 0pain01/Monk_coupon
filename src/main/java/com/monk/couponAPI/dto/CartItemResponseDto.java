package com.monk.couponAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDto {
    private Long product_id;
    private int quantity;
    private BigDecimal price;
    private BigDecimal total_discount;
}
