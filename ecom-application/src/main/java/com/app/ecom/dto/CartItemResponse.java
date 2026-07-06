package com.app.ecom.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long product_id;
    private Integer quantity;
}
