package com.monk.couponAPI.service.impl;

import com.monk.couponAPI.dto.*;
import com.monk.couponAPI.entity.*;
import com.monk.couponAPI.enums.CouponType;
import com.monk.couponAPI.exception.CouponExpiredException;
import com.monk.couponAPI.exception.CouponNotFoundException;
import com.monk.couponAPI.repository.*;
import com.monk.couponAPI.service.CouponService;
import com.monk.couponAPI.service.impl.strategy.CouponStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CartWiseCouponRepository cartRepo;
    private final ProductWiseCouponRepository productRepo;
    private final BxGyCouponRepository bxgyRepo;
    private final BxProductRepository bxRepo;
    private final GyProductRepository gyRepo;
    private final List<CouponStrategy> strategies;

    @Override
    public Coupon createCoupon(CouponDto dto) {
        CouponType type = dto.getType();
        Coupon baseCoupon = switch (type) {
            case CART_WISE -> {
                CartWiseCoupon c = new CartWiseCoupon();
                c.setThreshold(new BigDecimal(dto.getDetails().get("threshold").toString()));
                c.setDiscountPercentage(Integer.parseInt(dto.getDetails().get("discount").toString()));
                yield c;
            }
            case PRODUCT_WISE -> {
                ProductWiseCoupon p = new ProductWiseCoupon();
                p.setProductId(Long.valueOf(dto.getDetails().get("product_id").toString()));
                p.setDiscountPercentage(Integer.parseInt(dto.getDetails().get("discount").toString()));
                yield p;
            }
            case BXGY -> {
                BxGyCoupon b = new BxGyCoupon();
                b.setRepetitionLimit(Integer.parseInt(dto.getDetails().get("repetition_limit").toString()));

                // Parse buy/get product lists using helper
                List<Map<String, Object>> buyList = parseProductList(dto.getDetails().get("buy_products"));
                List<Map<String, Object>> getList = parseProductList(dto.getDetails().get("get_products"));

                List<BxProduct> buyProducts = mapToBxProducts(buyList, b);
                List<GyProduct> getProducts = mapToGyProducts(getList, b);

                b.setBuyProducts(buyProducts);
                b.setGetProducts(getProducts);
                yield b;
            }
        };

        baseCoupon.setName(dto.getName());
        baseCoupon.setDescription(dto.getDescription());
        baseCoupon.setType(type);
        baseCoupon.setExpiresAt(dto.getExpiresAt());
        baseCoupon.setActive(dto.isActive());

        return switch (type) {
            case CART_WISE -> cartRepo.save((CartWiseCoupon) baseCoupon);
            case PRODUCT_WISE -> productRepo.save((ProductWiseCoupon) baseCoupon);
            case BXGY -> bxgyRepo.save((BxGyCoupon) baseCoupon);
        };
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public Coupon getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with ID " + couponId));
    }

    @Override
    public Coupon updateCoupon(Long couponId, CouponDto dto) {
        Coupon existing = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with ID " + couponId));

        // Type shouldn't change for update
        if (!existing.getType().equals(dto.getType())) {
            throw new IllegalArgumentException("Cannot change coupon type on update");
        }

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setExpiresAt(dto.getExpiresAt());
        existing.setActive(dto.isActive());

        switch (existing.getType()) {
            case CART_WISE -> {
                CartWiseCoupon c = (CartWiseCoupon) existing;
                c.setThreshold(new BigDecimal(dto.getDetails().get("threshold").toString()));
                c.setDiscountPercentage(Integer.parseInt(dto.getDetails().get("discount").toString()));
                return cartRepo.save(c);
            }
            case PRODUCT_WISE -> {
                ProductWiseCoupon p = (ProductWiseCoupon) existing;
                p.setProductId(Long.valueOf(dto.getDetails().get("product_id").toString()));
                p.setDiscountPercentage(Integer.parseInt(dto.getDetails().get("discount").toString()));
                return productRepo.save(p);
            }
            case BXGY -> {
                BxGyCoupon b = (BxGyCoupon) existing;
                b.setRepetitionLimit(Integer.parseInt(dto.getDetails().get("repetition_limit").toString()));

                // Delete old associations
                bxRepo.deleteAll(b.getBuyProducts());
                gyRepo.deleteAll(b.getGetProducts());

                // Parse new lists
                List<Map<String, Object>> buyList = parseProductList(dto.getDetails().get("buy_products"));
                List<Map<String, Object>> getList = parseProductList(dto.getDetails().get("get_products"));

                b.setBuyProducts(mapToBxProducts(buyList, b));
                b.setGetProducts(mapToGyProducts(getList, b));

                return bxgyRepo.save(b);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public String deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with ID " + id));
        String name = coupon.getName();

        // Delete based on coupon type
        switch (coupon.getType()) {
            case CART_WISE -> cartRepo.deleteById(id);
            case PRODUCT_WISE -> productRepo.deleteById(id);
            case BXGY -> bxgyRepo.deleteById(id);
        }

        return name;
    }

    @Override
    public ApplicableCouponResponse getApplicableCoupons(CartDto cart) {
        List<ApplicableCoupon> applicable = couponRepository.findAll().stream()
                .filter(coupon -> strategies.stream()
                        .filter(s -> s.supports(coupon.getType()))
                        .anyMatch(s -> s.isApplicable(coupon, cart)))
                .map(coupon -> {
                    CouponStrategy strategy = strategies.stream()
                            .filter(s -> s.supports(coupon.getType()))
                            .findFirst().orElseThrow();
                    BigDecimal discount = strategy.calculateDiscount(coupon, cart);

                    return new ApplicableCoupon(
                            coupon.getId(),
                            coupon.getType().name().toLowerCase().replace("_", "-"),
                            discount
                    );
                })
                .collect(Collectors.toList());

        return new ApplicableCouponResponse(applicable);
    }

    @Override
    public ApplyCouponResponse applyCoupon(Long couponId, CartDto cart) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with ID " + couponId));
        if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            if (coupon.isActive()) {
                coupon.setActive(false);
                couponRepository.save(coupon); // persist deactivation
            }
            throw new CouponExpiredException("Coupon with ID " + couponId + " has expired.");
        }

        if (!coupon.isActive()) {
            throw new CouponExpiredException("Coupon with ID " + couponId + " is inactive or expired.");
        }
        CouponStrategy strategy = strategies.stream()
                .filter(s -> s.supports(coupon.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No strategy found for coupon type"));

        BigDecimal discount = strategy.calculateDiscount(coupon, cart).setScale(2, RoundingMode.HALF_UP);
        cart = strategy.applyCoupon(coupon, cart);
        List<CartItemResponseDto> updatedItems = cart.getItems().stream()
                .map(item -> {
                    CartItemResponseDto dto = new CartItemResponseDto();
                    dto.setProduct_id(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    dto.setTotal_discount( item.getTotalDiscount() != null ? item.getTotalDiscount() : BigDecimal.ZERO); // Placeholder, customize if needed
                    return dto;
                })
                .collect(Collectors.toList());

        BigDecimal totalPrice = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        BigDecimal finalPrice = totalPrice.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        UpdatedCartResponse updatedCart = new UpdatedCartResponse(
                updatedItems,
                totalPrice,
                discount,
                finalPrice
        );

        return new ApplyCouponResponse(updatedCart);
    }

    private List<Map<String, Object>> parseProductList(Object rawInput) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (rawInput instanceof List<?> rawList) {
            for (Object item : rawList) {
                if (item instanceof Map<?, ?> rawMap) {
                    Map<String, Object> castedMap = new HashMap<>();
                    for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                        if (entry.getKey() instanceof String key) {
                            castedMap.put(key, entry.getValue());
                        }
                    }
                    result.add(castedMap);
                }
            }
        }
        return result;
    }

    private List<BxProduct> mapToBxProducts(List<Map<String, Object>> maps, BxGyCoupon coupon) {
        return maps.stream().map(m -> {
            BxProduct bx = new BxProduct();
            bx.setProductId(Long.valueOf(m.get("product_id").toString()));
            bx.setQuantityRequired(Integer.parseInt(m.get("quantity").toString()));
            bx.setCoupon(coupon);
            return bx;
        }).toList();
    }

    private List<GyProduct> mapToGyProducts(List<Map<String, Object>> maps, BxGyCoupon coupon) {
        return maps.stream().map(m -> {
            GyProduct gy = new GyProduct();
            gy.setProductId(Long.valueOf(m.get("product_id").toString()));
            gy.setQuantityFree(Integer.parseInt(m.get("quantity").toString()));
            gy.setCoupon(coupon);
            return gy;
        }).toList();
    }
}
