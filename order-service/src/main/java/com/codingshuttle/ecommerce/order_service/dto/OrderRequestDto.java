package com.codingshuttle.ecommerce.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDto {
    private Long id;
    private List<OrderRequestItemDto> orderItem;  // match entity field
    private Double price;                         // match entity field
}
