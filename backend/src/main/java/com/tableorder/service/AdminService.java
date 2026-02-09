package com.tableorder.service;

import com.tableorder.domain.Admin;
import com.tableorder.domain.Order;
import com.tableorder.domain.OrderStatus;
import com.tableorder.dto.AdminLoginResponseDto;
import com.tableorder.dto.OrderResponseDto;
import com.tableorder.exception.*;
import com.tableorder.mapper.AdminMapper;
import com.tableorder.mapper.OrderMapper;
import com.tableorder.mapper.TableSessionMapper;
import com.tableorder.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final AdminMapper adminMapper;
    private final OrderMapper orderMapper;
    private final TableSessionMapper tableSessionMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public AdminLoginResponseDto login(String username, String password) {
        Admin admin = adminMapper.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        
        System.out.println("DEBUG: Found admin - username: " + admin.getUsername());
        System.out.println("DEBUG: Stored password hash: " + admin.getPassword());
        System.out.println("DEBUG: Input password: " + password);
        System.out.println("DEBUG: Password match result: " + passwordEncoder.matches(password, admin.getPassword()));
        
        if (admin.getLockedUntil() != null && admin.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException("Account locked. Try again after 30 minutes");
        }
        
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            int attempts = admin.getLoginAttempts() + 1;
            LocalDateTime lockedUntil = null;
            
            if (attempts >= 5) {
                lockedUntil = LocalDateTime.now().plusMinutes(30);
            }
            
            adminMapper.updateLoginAttempts(admin.getId(), attempts, lockedUntil);
            
            if (attempts >= 5) {
                throw new AccountLockedException("Account locked. Try again after 30 minutes");
            }
            
            throw new AuthenticationException("Invalid username or password");
        }
        
        adminMapper.updateLoginAttempts(admin.getId(), 0, null);
        
        String token = jwtTokenProvider.generateToken(admin);
        return new AdminLoginResponseDto(token);
    }
    
    public List<OrderResponseDto> getOrders(Long storeId) {
        List<Long> activeSessionIds = tableSessionMapper.findActiveByStoreId(storeId)
                .stream()
                .map(session -> session.getId())
                .collect(Collectors.toList());
        
        if (activeSessionIds.isEmpty()) {
            return List.of();
        }
        
        return orderMapper.findBySessionIdIn(activeSessionIds)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderMapper.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }
        
        orderMapper.updateStatus(orderId, newStatus);
    }
    
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderMapper.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        if (order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot delete order in " + order.getStatus() + " status");
        }
        
        orderMapper.deleteById(orderId);
    }
    
    private boolean isValidTransition(OrderStatus current, OrderStatus target) {
        if (target == OrderStatus.CANCELLED) {
            return true;
        }
        
        return switch (current) {
            case PENDING -> target == OrderStatus.PREPARING;
            case PREPARING -> target == OrderStatus.COMPLETED;
            default -> false;
        };
    }
    
    private OrderResponseDto toDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .tableId(order.getTableId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
