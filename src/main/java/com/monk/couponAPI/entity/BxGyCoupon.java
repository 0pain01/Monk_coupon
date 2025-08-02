package com.monk.couponAPI.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.monk.couponAPI.enums.CouponType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class BxGyCoupon extends Coupon{
    private Integer repetitionLimit;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("bx-products")
    private List<BxProduct> buyProducts;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("gy-products")
    private List<GyProduct> getProducts;

    public BxGyCoupon(Integer repetitionLimit, List<BxProduct> buyProducts, List<GyProduct> getProducts) {
        this.repetitionLimit = repetitionLimit;
        this.buyProducts = buyProducts;
        this.getProducts = getProducts;
    }

    public BxGyCoupon(long id, CouponType type, String description, String name, LocalDateTime expiresAt, boolean active, Integer repetitionLimit, List<BxProduct> buyProducts, List<GyProduct> getProducts) {
        super(id, type, description, name, expiresAt, active);
        this.repetitionLimit = repetitionLimit;
        this.buyProducts = buyProducts;
        this.getProducts = getProducts;
    }

}
