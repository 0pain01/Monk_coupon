package com.monk.couponAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.couponAPI.controller.CouponController;
import com.monk.couponAPI.dto.*;
import com.monk.couponAPI.entity.Coupon;
import com.monk.couponAPI.entity.ProductWiseCoupon;
import com.monk.couponAPI.enums.CouponType;
import com.monk.couponAPI.exception.CouponNotFoundException;
import com.monk.couponAPI.service.CouponService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
public class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void testCreateCoupon_success() throws Exception {
        CouponDto dto = new CouponDto(
                "Test Coupon",
                "desc",
                CouponType.PRODUCT_WISE,
                LocalDateTime.now().plusDays(5),true,
                Map.of("product_id", 1, "discount", 10) // assuming the details map is needed
        );

        ProductWiseCoupon created = new ProductWiseCoupon();
        created.setId(1L);
        created.setName(dto.getName());
        created.setDescription(dto.getDescription());
        created.setType(CouponType.PRODUCT_WISE);
        created.setExpiresAt(dto.getExpiresAt());
        created.setActive(true);
        created.setProductId(1L);
        created.setDiscountPercentage(10);

        Mockito.when(couponService.createCoupon(any())).thenReturn(created);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCouponById_notFound() throws Exception {
        Mockito.when(couponService.getCouponById(99L)).thenThrow(new CouponNotFoundException("Coupon not found"));

        mockMvc.perform(get("/coupons/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllCoupons() throws Exception {
        Mockito.when(couponService.getAllCoupons()).thenReturn(List.of());

        mockMvc.perform(get("/coupons"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCoupon_success() throws Exception {
        CouponDto dto = new CouponDto(
                "Updated",
                "desc",
                CouponType.PRODUCT_WISE,
                LocalDateTime.now().plusDays(5),true,
                Map.of("product_id", 1, "discount", 10) // Replace with appropriate details
        );

        ProductWiseCoupon updated = new ProductWiseCoupon();
        updated.setId(1L);
        updated.setName("Updated");
        updated.setDescription("desc");
        updated.setType(CouponType.PRODUCT_WISE);
        updated.setExpiresAt(dto.getExpiresAt());
        updated.setActive(true);
        updated.setProductId(1L);
        updated.setDiscountPercentage(10);

        Mockito.when(couponService.updateCoupon(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/coupons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteCoupon_success() throws Exception {
        Long couponId = 1L;
        String couponName = "SampleCoupon";

        // Mock the service to return only the coupon name (not the full message)
        Mockito.when(couponService.deleteCoupon(couponId)).thenReturn(couponName);

        mockMvc.perform(delete("/coupons/{id}", couponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted coupon: " + couponName + " (ID: " + couponId + ")"));
    }

    @Test
    void testGetCouponById_success() throws Exception {
        CouponDto dto = new CouponDto(
                "Test Coupon",
                "desc",
                CouponType.PRODUCT_WISE,
                LocalDateTime.now().plusDays(5),true,
                Map.of("product_id", 1, "discount", 10) // assuming the details map is needed
        );

        ProductWiseCoupon created = new ProductWiseCoupon();
        created.setId(1L);

        Mockito.when(couponService.getCouponById(1L)).thenReturn(created);

        mockMvc.perform(get("/coupons/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testApplyCoupon_success() throws Exception {
        Long couponId = 1L;

        CartDto inputCart = new CartDto(List.of(new CartItemDto(1L, 2, BigDecimal.valueOf(50))));
        ApplyCouponResponse mockResponse = new ApplyCouponResponse(new UpdatedCartResponse(
                List.of(new CartItemResponseDto(1L, 2, BigDecimal.valueOf(50), BigDecimal.valueOf(10))),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(90)
        ));

        when(couponService.applyCoupon(eq(couponId), any(CartDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/coupons/apply-coupon/1", couponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "cart": {
                        "items": [
                          { "product_id": 1, "quantity": 2, "price": 50 }
                        ]
                      }
                    }
                """))
                .andExpect(status().isOk());

        verify(couponService).applyCoupon(eq(couponId), any(CartDto.class));
    }
    @Test
    void testGetApplicableCoupons_success() throws Exception {
        CartDto cart = new CartDto(List.of());
        CartRequestWrapper wrapper = new CartRequestWrapper(cart);

        // Dummy response to return
        ApplicableCouponResponse dummyResponse = new ApplicableCouponResponse();
        dummyResponse.setApplicableCoupons(List.of()); // or populate with mock data if needed

        // Mock the service call
        Mockito.when(couponService.getApplicableCoupons(any())).thenReturn(dummyResponse);

        // Perform the request and verify the result
        mockMvc.perform(post("/coupons/applicable-coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrapper)))
                .andExpect(status().isOk());
    }

    @Test
    void testApplyCoupon_couponNotFound() throws Exception {
        Long couponId = 1L;

        // Simulate the service throwing a CouponNotFoundException
        Mockito.when(couponService.applyCoupon(eq(couponId), any()))
                .thenThrow(new CouponNotFoundException("Coupon Not found "+couponId));

        String requestBody = """
    {
      "cart": {
        "items": [
          { "product_id": 1, "quantity": 2, "price": 50 }
        ]
      }
    }
    """;

        mockMvc.perform(post("/coupons/apply-coupon/{id}", couponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }
}
