package com.monk.couponAPI.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Name;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CartItemDto {

    @JsonProperty("product_id")
    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    private BigDecimal totalDiscount = BigDecimal.ZERO;

    public CartItemDto(Long productId, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

}
