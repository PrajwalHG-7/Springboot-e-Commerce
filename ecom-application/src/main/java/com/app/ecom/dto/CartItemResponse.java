package com.app.ecom.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long product_id;
    private Integer quantity;
    private BigDecimal price;
}
