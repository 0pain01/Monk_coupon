package com.monk.couponAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedCartResponse {
    private List<CartItemResponseDto> items;
    private BigDecimal total_price;
    private BigDecimal total_discount;
    private BigDecimal final_price;
}
