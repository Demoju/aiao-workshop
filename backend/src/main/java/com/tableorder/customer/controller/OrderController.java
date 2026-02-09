package com.tableorder.customer.controller;

import com.tableorder.customer.dto.OrderRequestDto;
import com.tableorder.customer.dto.OrderResponseDto;
import com.tableorder.customer.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid OrderRequestDto request) {
        OrderResponseDto response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(
            @RequestParam Long tableId,
            @RequestParam String sessionId) {
        List<OrderResponseDto> orders = orderService.getOrders(tableId, sessionId);
        return ResponseEntity.ok(orders);
    }
    
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
