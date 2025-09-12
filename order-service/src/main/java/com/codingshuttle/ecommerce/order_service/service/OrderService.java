package com.codingshuttle.ecommerce.order_service.service;

import com.codingshuttle.ecommerce.order_service.clients.InventoryOpenFeignClient;
import com.codingshuttle.ecommerce.order_service.dto.OrderRequestDto;
import com.codingshuttle.ecommerce.order_service.entity.OrderItem;
import com.codingshuttle.ecommerce.order_service.entity.OrderStatus;
import com.codingshuttle.ecommerce.order_service.entity.Orders;
import com.codingshuttle.ecommerce.order_service.repository.OrderRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;

    public List<OrderRequestDto> getAllOrders(){
        log.info("Fetching all orders");
        List<Orders> orders = orderRepository.findAll();
        return orders.stream().map(order-> modelMapper.map(order, OrderRequestDto.class)).toList();
    }

    public OrderRequestDto getOrderById(Long id){
        log.info("Fetching the order with id: {}",id);
        Orders order = orderRepository.findById(id).orElseThrow(()-> new RuntimeException("Order not found"));
        return modelMapper.map(order,OrderRequestDto.class);
    }

    @Retry(name = "inventoryRetry", fallbackMethod = "createOrderFallback")
    @RateLimiter(name = "inventoryRateLimiter",fallbackMethod = "createOrderFallback")
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);
        Orders orders = modelMapper.map(orderRequestDto,Orders.class);

        for(OrderItem orderItem:orders.getOrderItem()){
            orderItem.setOrder(orders);
        }

        orders.setPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);

        Orders savedOrders = orderRepository.save(orders);
        return modelMapper.map(savedOrders, OrderRequestDto.class);
    }

    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto,Throwable throwable) {
        log.error("Fallback error occurred due to: " + throwable.getMessage());
        return new OrderRequestDto();
    }
}