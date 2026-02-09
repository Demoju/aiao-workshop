package com.tableorder.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.customer.dto.*;
import com.tableorder.customer.service.OrderService;
import com.tableorder.domain.OrderStatus;
import com.tableorder.exception.InvalidOrderStatusException;
import com.tableorder.exception.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private OrderService orderService;
    
    @Test
    @DisplayName("주문 생성 API 성공")
    void createOrder_Success() throws Exception {
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
        
        OrderItemResponseDto responseItem = OrderItemResponseDto.builder()
                .orderItemId(1L)
                .menuId(1L)
                .menuName("김치찌개")
                .quantity(2)
                .unitPrice(new BigDecimal("8000"))
                .build();
        
        OrderResponseDto response = OrderResponseDto.builder()
                .orderId(1L)
                .orderNumber("ORD-20260209-001")
                .tableId(1L)
                .sessionId("session-123")
                .totalAmount(new BigDecimal("16000"))
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .items(Arrays.asList(responseItem))
                .build();
        
        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/customer/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-20260209-001"))
                .andExpect(jsonPath("$.totalAmount").value(16000))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items.length()").value(1));
    }
    
    @Test
    @DisplayName("주문 생성 API 실패 - 필수 필드 누락")
    void createOrder_ValidationError() throws Exception {
        // Given
        OrderRequestDto request = new OrderRequestDto();
        // 필수 필드 누락
        
        // When & Then
        mockMvc.perform(post("/api/customer/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("주문 내역 조회 API 성공")
    void getOrders_Success() throws Exception {
        // Given
        OrderItemResponseDto item1 = OrderItemResponseDto.builder()
                .orderItemId(1L)
                .menuId(1L)
                .menuName("김치찌개")
                .quantity(2)
                .unitPrice(new BigDecimal("8000"))
                .build();
        
        OrderResponseDto order1 = OrderResponseDto.builder()
                .orderId(1L)
                .orderNumber("ORD-20260209-001")
                .tableId(1L)
                .sessionId("session-123")
                .totalAmount(new BigDecimal("16000"))
                .status(OrderStatus.PENDING)
                .orderTime(LocalDateTime.now())
                .items(Arrays.asList(item1))
                .build();
        
        List<OrderResponseDto> orders = Arrays.asList(order1);
        when(orderService.getOrders(1L, "session-123")).thenReturn(orders);
        
        // When & Then
        mockMvc.perform(get("/api/customer/orders")
                        .param("tableId", "1")
                        .param("sessionId", "session-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD-20260209-001"));
    }
    
    @Test
    @DisplayName("주문 내역 조회 API 실패 - 필수 파라미터 누락")
    void getOrders_MissingParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/customer/orders")
                        .param("tableId", "1"))
                // sessionId 누락
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("주문 취소 API 성공")
    void cancelOrder_Success() throws Exception {
        // Given
        doNothing().when(orderService).cancelOrder(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/customer/orders/1"))
                .andExpect(status().isNoContent());
        
        verify(orderService).cancelOrder(1L);
    }
    
    @Test
    @DisplayName("주문 취소 API 실패 - 주문 없음")
    void cancelOrder_OrderNotFound() throws Exception {
        // Given
        doThrow(new OrderNotFoundException()).when(orderService).cancelOrder(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/customer/orders/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("주문 취소 API 실패 - 잘못된 주문 상태")
    void cancelOrder_InvalidStatus() throws Exception {
        // Given
        doThrow(new InvalidOrderStatusException()).when(orderService).cancelOrder(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/customer/orders/1"))
                .andExpect(status().isBadRequest());
    }
}
