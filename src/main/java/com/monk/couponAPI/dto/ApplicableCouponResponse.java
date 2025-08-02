package com.monk.couponAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicableCouponResponse {
    @JsonProperty("applicable_coupons")
    private List<ApplicableCoupon> applicableCoupons;
}
