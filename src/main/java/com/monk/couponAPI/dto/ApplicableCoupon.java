package com.monk.couponAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ApplicableCoupon {
    @JsonProperty("coupon_id")
    private Long couponId;

    private String type;

    private BigDecimal discount;
}
