package com.monk.couponAPI.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class ProductWiseCoupon extends Coupon{
    private Long productId;
    private Integer discountPercentage;
}
