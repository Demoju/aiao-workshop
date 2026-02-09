package com.tableorder.customer.service;

import com.tableorder.customer.dto.*;
import com.tableorder.customer.mapper.MenuMapper;
import com.tableorder.customer.mapper.OrderItemMapper;
import com.tableorder.customer.mapper.OrderMapper;
import com.tableorder.domain.*;
import com.tableorder.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderMapper orderMapper;
    
    @Mock
    private OrderItemMapper orderItemMapper;
    
    @Mock
    private MenuMapper menuMapper;
    
    @Mock
    private SessionService sessionService;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() {
        // Given
        OrderItemRequestDto item1 = new OrderItemRequestDto();
        item1.setMenuId(1L);
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("8000"));
        
        OrderRequestDto request = new OrderRequestDto();
        request.setTableId(1L);
        request.setSessionId("session-123");
        request.setItems(Arrays.asList(item1));
        request.setTotalAmount(new BigDecimal("16000"));
        
        Menu menu = new Menu();
        menu.setMenuId(1L);
        menu.setMenuName("김치찌개");
        menu.setPrice(new BigDecimal("8000"));
        menu.setIsAvailable(true);
        
        TableSession session = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .isActive(true)
                .build();
        
        Order savedOrder = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-20260209-001")
                .tableId(1L)
                .sessionId("session-123")
                .totalAmount(new BigDecimal("16000"))
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .build();
        
        OrderItem orderItem = OrderItem.builder()
                .orderItemId(1L)
                .orderId(1L)
                .menuId(1L)
                .menuName("김치찌개")
                .quantity(2)
                .unitPrice(new BigDecimal("8000"))
                .build();
        
        when(sessionService.getOrCreateSession(1L)).thenReturn(session);
        when(menuMapper.selectMenuById(1L)).thenReturn(menu);
        when(orderMapper.countTodayOrders()).thenReturn(0);
        doAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(1L);
            order.setOrderNumber("ORD-20260209-001");
            return null;
        }).when(orderMapper).insertOrder(any(Order.class));
        when(orderItemMapper.selectOrderItemsByOrderId(1L)).thenReturn(Arrays.asList(orderItem));
        
        // When
        OrderResponseDto response = orderService.createOrder(request);
        
        // Then
        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("16000"));
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getItems()).hasSize(1);
        verify(sessionService).getOrCreateSession(1L);
        verify(menuMapper).selectMenuById(1L);
        verify(orderMapper).insertOrder(any(Order.class));
        verify(orderItemMapper).insertOrderItems(anyList());
    }
    
    @Test
    @DisplayName("주문 생성 실패 - 메뉴 없음")
    void createOrder_MenuNotFound() {
        // Given
        OrderItemRequestDto item1 = new OrderItemRequestDto();
        item1.setMenuId(999L);
        item1.setQuantity(1);
        item1.setUnitPrice(new BigDecimal("8000"));
        
        OrderRequestDto request = new OrderRequestDto();
        request.setTableId(1L);
        request.setSessionId("session-123");
        request.setItems(Arrays.asList(item1));
        request.setTotalAmount(new BigDecimal("8000"));
        
        TableSession session = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .isActive(true)
                .build();
        
        when(sessionService.getOrCreateSession(1L)).thenReturn(session);
        when(menuMapper.selectMenuById(999L)).thenReturn(null);
        
        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(MenuNotFoundException.class);
        verify(orderMapper, never()).insertOrder(any());
    }
    
    @Test
    @DisplayName("주문 생성 실패 - 품절 메뉴")
    void createOrder_MenuNotAvailable() {
        // Given
        OrderItemRequestDto item1 = new OrderItemRequestDto();
        item1.setMenuId(1L);
        item1.setQuantity(1);
        item1.setUnitPrice(new BigDecimal("8000"));
        
        OrderRequestDto request = new OrderRequestDto();
        request.setTableId(1L);
        request.setSessionId("session-123");
        request.setItems(Arrays.asList(item1));
        request.setTotalAmount(new BigDecimal("8000"));
        
        Menu menu = new Menu();
        menu.setMenuId(1L);
        menu.setMenuName("냉면");
        menu.setPrice(new BigDecimal("8000"));
        menu.setIsAvailable(false);
        
        TableSession session = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .isActive(true)
                .build();
        
        when(sessionService.getOrCreateSession(1L)).thenReturn(session);
        when(menuMapper.selectMenuById(1L)).thenReturn(menu);
        
        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(MenuNotAvailableException.class);
        verify(orderMapper, never()).insertOrder(any());
    }
    
    @Test
    @DisplayName("주문 생성 실패 - 가격 불일치")
    void createOrder_PriceChanged() {
        // Given
        OrderItemRequestDto item1 = new OrderItemRequestDto();
        item1.setMenuId(1L);
        item1.setQuantity(1);
        item1.setUnitPrice(new BigDecimal("7000")); // 실제 가격과 다름
        
        OrderRequestDto request = new OrderRequestDto();
        request.setTableId(1L);
        request.setSessionId("session-123");
        request.setItems(Arrays.asList(item1));
        request.setTotalAmount(new BigDecimal("7000"));
        
        Menu menu = new Menu();
        menu.setMenuId(1L);
        menu.setMenuName("김치찌개");
        menu.setPrice(new BigDecimal("8000")); // 실제 가격
        menu.setIsAvailable(true);
        
        TableSession session = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .isActive(true)
                .build();
        
        when(sessionService.getOrCreateSession(1L)).thenReturn(session);
        when(menuMapper.selectMenuById(1L)).thenReturn(menu);
        
        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(PriceChangedException.class);
        verify(orderMapper, never()).insertOrder(any());
    }
    
    @Test
    @DisplayName("주문 생성 실패 - 총액 불일치")
    void createOrder_TotalAmountMismatch() {
        // Given
        OrderItemRequestDto item1 = new OrderItemRequestDto();
        item1.setMenuId(1L);
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("8000"));
        
        OrderRequestDto request = new OrderRequestDto();
        request.setTableId(1L);
        request.setSessionId("session-123");
        request.setItems(Arrays.asList(item1));
        request.setTotalAmount(new BigDecimal("15000")); // 잘못된 총액
        
        Menu menu = new Menu();
        menu.setMenuId(1L);
        menu.setMenuName("김치찌개");
        menu.setPrice(new BigDecimal("8000"));
        menu.setIsAvailable(true);
        
        TableSession session = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .isActive(true)
                .build();
        
        when(sessionService.getOrCreateSession(1L)).thenReturn(session);
        when(menuMapper.selectMenuById(1L)).thenReturn(menu);
        
        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(TotalAmountMismatchException.class);
        verify(orderMapper, never()).insertOrder(any());
    }
    
    @Test
    @DisplayName("주문 내역 조회 성공")
    void getOrders_Success() {
        // Given
        Order order1 = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-20260209-001")
                .tableId(1L)
                .sessionId("session-123")
                .totalAmount(new BigDecimal("16000"))
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .build();
        
        OrderItem item1 = OrderItem.builder()
                .orderItemId(1L)
                .orderId(1L)
                .menuId(1L)
                .menuName("김치찌개")
                .quantity(2)
                .unitPrice(new BigDecimal("8000"))
                .build();
        
        when(orderMapper.selectOrdersBySession(1L, "session-123"))
                .thenReturn(Arrays.asList(order1));
        when(orderItemMapper.selectOrderItemsByOrderId(1L))
                .thenReturn(Arrays.asList(item1));
        
        // When
        List<OrderResponseDto> result = orderService.getOrders(1L, "session-123");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderNumber()).isEqualTo("ORD-20260209-001");
        assertThat(result.get(0).getItems()).hasSize(1);
        verify(orderMapper).selectOrdersBySession(1L, "session-123");
    }
    
    @Test
    @DisplayName("주문 취소 성공")
    void cancelOrder_Success() {
        // Given
        Order order = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-20260209-001")
                .status(OrderStatus.PENDING)
                .build();
        
        when(orderMapper.selectOrderById(1L)).thenReturn(order);
        
        // When
        orderService.cancelOrder(1L);
        
        // Then
        verify(orderMapper).selectOrderById(1L);
        verify(orderItemMapper).deleteOrderItems(1L);
        verify(orderMapper).deleteOrder(1L);
    }
    
    @Test
    @DisplayName("주문 취소 실패 - 주문 없음")
    void cancelOrder_OrderNotFound() {
        // Given
        when(orderMapper.selectOrderById(999L)).thenReturn(null);
        
        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(999L))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderMapper, never()).deleteOrder(any());
    }
    
    @Test
    @DisplayName("주문 취소 실패 - 잘못된 주문 상태")
    void cancelOrder_InvalidStatus() {
        // Given
        Order order = Order.builder()
                .orderId(1L)
                .orderNumber("ORD-20260209-001")
                .status(OrderStatus.ACCEPTED) // PENDING이 아님
                .build();
        
        when(orderMapper.selectOrderById(1L)).thenReturn(order);
        
        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(InvalidOrderStatusException.class);
        verify(orderMapper, never()).deleteOrder(any());
    }
}
