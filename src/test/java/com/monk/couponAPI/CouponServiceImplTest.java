package com.monk.couponAPI;

import com.monk.couponAPI.dto.CartDto;
import com.monk.couponAPI.entity.Coupon;
import com.monk.couponAPI.entity.ProductWiseCoupon;
import com.monk.couponAPI.exception.CouponExpiredException;
import com.monk.couponAPI.repository.CouponRepository;
import com.monk.couponAPI.service.impl.CouponServiceImpl;
import com.monk.couponAPI.service.impl.strategy.CouponStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponServiceImplTest {
    @Mock
    CouponRepository couponRepository;
    @Mock
    CouponStrategy strategy;
    @InjectMocks
    CouponServiceImpl service;

    @Test
    void testApplyCoupon_throwsExceptionWhenExpired() {
        Coupon expiredCoupon = new ProductWiseCoupon();
        expiredCoupon.setId(10L);
        expiredCoupon.setActive(true);
        expiredCoupon.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(couponRepository.findById(10L)).thenReturn(Optional.of(expiredCoupon));

        CartDto cart = new CartDto(List.of());

        assertThrows(CouponExpiredException.class, () -> {
            service.applyCoupon(10L, cart);
        });

        verify(couponRepository).save(expiredCoupon); // deactivation
    }
}
