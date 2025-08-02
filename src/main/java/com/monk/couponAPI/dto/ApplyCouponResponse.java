package com.monk.couponAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyCouponResponse {
    @JsonProperty("updated_cart")
    private UpdatedCartResponse updatedCart;
}
