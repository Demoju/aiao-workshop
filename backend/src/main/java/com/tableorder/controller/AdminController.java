package com.tableorder.controller;

import com.tableorder.domain.OrderStatus;
import com.tableorder.dto.AdminLoginRequestDto;
import com.tableorder.dto.AdminLoginResponseDto;
import com.tableorder.dto.OrderResponseDto;
import com.tableorder.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final AdminService adminService;
    
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDto> login(@RequestBody AdminLoginRequestDto request) {
        AdminLoginResponseDto response = adminService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/test-password")
    public ResponseEntity<String> testPassword(@RequestBody AdminLoginRequestDto request) {
        return ResponseEntity.ok("Received: " + request.getUsername() + " / " + request.getPassword());
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(@RequestParam Long storeId) {
        List<OrderResponseDto> orders = adminService.getOrders(storeId);
        return ResponseEntity.ok(orders);
    }
    
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        adminService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        adminService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
