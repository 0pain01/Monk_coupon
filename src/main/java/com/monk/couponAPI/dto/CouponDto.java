package com.monk.couponAPI.dto;

import com.monk.couponAPI.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponDto {
    private String name;
    private String description;
    private CouponType type;
    private LocalDateTime expiresAt;
    private boolean active;

    private Map<String, Object> details;
}
