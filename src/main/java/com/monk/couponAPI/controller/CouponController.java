package com.monk.couponAPI.controller;

import com.monk.couponAPI.dto.*;
import com.monk.couponAPI.entity.Coupon;
import com.monk.couponAPI.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponDto dto) {
        return ResponseEntity.ok(couponService.createCoupon(dto));
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody CouponDto dto) {
        return ResponseEntity.ok(couponService.updateCoupon(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCoupon(@PathVariable Long id) {
        String name = couponService.deleteCoupon(id); // Assume this method returns the deleted coupon's name
        Map<String, String> response = new HashMap<>();
        response.put("message", "Deleted coupon: " + name + " (ID: " + id + ")");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<ApplicableCouponResponse> getApplicableCoupons(@RequestBody CartRequestWrapper wrapper) {
        return ResponseEntity.ok(couponService.getApplicableCoupons(wrapper.getCart()));
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(@PathVariable("id") Long id,@RequestBody CartRequestWrapper wrapper) {
        return ResponseEntity.ok(couponService.applyCoupon(id, wrapper.getCart()));
    }
}
