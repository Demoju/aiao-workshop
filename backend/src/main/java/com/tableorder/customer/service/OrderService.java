package com.tableorder.customer.service;

import com.tableorder.customer.dto.*;
import com.tableorder.customer.mapper.MenuMapper;
import com.tableorder.customer.mapper.OrderItemMapper;
import com.tableorder.customer.mapper.OrderMapper;
import com.tableorder.domain.*;
import com.tableorder.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final MenuMapper menuMapper;
    private final SessionService sessionService;
    
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        log.info("주문 생성 시작 - tableId: {}, sessionId: {}, totalAmount: {}", 
                request.getTableId(), request.getSessionId(), request.getTotalAmount());
        
        // 1. 세션 검증 및 생성
        sessionService.getOrCreateSession(request.getTableId());
        
        // 2. 메뉴 검증 및 가격 검증
        validateMenusAndPrices(request.getItems());
        
        // 3. 총액 검증
        validateTotalAmount(request.getItems(), request.getTotalAmount());
        
        // 4. 주문 번호 생성
        String orderNumber = generateOrderNumber();
        
        // 5. Order 엔티티 생성
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .tableId(request.getTableId())
                .sessionId(Long.parseLong(request.getSessionId()))
                .totalAmount(request.getTotalAmount())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        orderMapper.insertOrder(order);
        
        // 6. OrderItem 엔티티 생성
        List<OrderItem> orderItems = request.getItems().stream()
                .map(itemDto -> OrderItem.builder()
                        .orderId(order.getId())
                        .menuId(itemDto.getMenuId())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .build())
                .collect(Collectors.toList());
        
        orderItemMapper.insertOrderItems(orderItems);
        
        log.info("주문 생성 완료 - orderId: {}, orderNumber: {}", 
                order.getId(), order.getOrderNumber());
        
        // 7. OrderItem 조회 (menuName 포함)
        List<OrderItem> itemsWithMenuName = orderItemMapper.selectOrderItemsByOrderId(order.getId());
        
        return buildOrderResponse(order, itemsWithMenuName);
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrders(Long tableId, String sessionId) {
        log.info("주문 내역 조회 - tableId: {}, sessionId: {}", tableId, sessionId);
        
        List<Order> orders = orderMapper.selectOrdersBySession(tableId, sessionId);
        
        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectOrderItemsByOrderId(order.getId());
                    return buildOrderResponse(order, items);
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("주문 취소 시작 - orderId: {}", orderId);
        
        // 1. 주문 조회
        Order order = orderMapper.selectOrderById(orderId);
        
        if (order == null) {
            throw new OrderNotFoundException("Order not found");
        }
        
        // 2. 주문 상태 확인
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException();
        }
        
        // 3. OrderItem 먼저 삭제
        orderItemMapper.deleteOrderItems(orderId);
        
        // 4. Order 삭제
        orderMapper.deleteOrder(orderId);
        
        log.info("주문 취소 완료 - orderId: {}", orderId);
    }
    
    private void validateMenusAndPrices(List<OrderItemRequestDto> items) {
        for (OrderItemRequestDto item : items) {
            Menu menu = menuMapper.selectMenuById(item.getMenuId());
            
            if (menu == null) {
                throw new MenuNotFoundException("메뉴 ID " + item.getMenuId() + "를 찾을 수 없습니다");
            }
            
            if (!menu.getIsAvailable()) {
                throw new MenuNotAvailableException("품절된 메뉴입니다: " + menu.getMenuName());
            }
            
            // 가격 검증
            if (menu.getPrice().compareTo(item.getUnitPrice()) != 0) {
                throw new PriceChangedException();
            }
        }
    }
    
    private void validateTotalAmount(List<OrderItemRequestDto> items, BigDecimal clientTotalAmount) {
        BigDecimal calculatedTotal = items.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (calculatedTotal.compareTo(clientTotalAmount) != 0) {
            throw new TotalAmountMismatchException();
        }
    }
    
    private String generateOrderNumber() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Integer count = orderMapper.countTodayOrders();
        int sequence = (count != null ? count : 0) + 1;
        return String.format("ORD-%s-%03d", today, sequence);
    }
    
    private OrderResponseDto buildOrderResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponseDto> itemDtos = items.stream()
                .map(item -> OrderItemResponseDto.builder()
                        .orderItemId(item.getOrderItemId())
                        .menuId(item.getMenuId())
                        .menuName(item.getMenuName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .tableId(order.getTableId())
                .sessionId(order.getSessionId().toString())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderTime(order.getCreatedAt())
                .items(itemDtos)
                .build();
    }
}
