package com.monk.couponAPI.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monk.couponAPI.enums.CouponType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
public abstract class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    private String name;
    private String description;
    private LocalDateTime expiresAt;
    private boolean active;

}
