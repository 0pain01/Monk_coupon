package com.monk.couponAPI.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class CartWiseCoupon extends Coupon{
    private BigDecimal threshold;
    private Integer discountPercentage;
}
