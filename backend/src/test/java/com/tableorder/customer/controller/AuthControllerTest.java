package com.tableorder.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.customer.dto.TableLoginRequestDto;
import com.tableorder.customer.dto.TableLoginResponseDto;
import com.tableorder.customer.service.AuthService;
import com.tableorder.exception.ErrorCode;
import com.tableorder.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthService authService;
    
    @Test
    @DisplayName("테이블 로그인 API 성공")
    void login_Success() throws Exception {
        // Given
        TableLoginRequestDto request = new TableLoginRequestDto();
        request.setStoreId(1L);
        request.setTableNumber("T01");
        request.setPassword("1234");
        
        TableLoginResponseDto response = new TableLoginResponseDto(
                1L, "session-123", 1L, "T01"
        );
        
        when(authService.login(any(TableLoginRequestDto.class))).thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId").value(1))
                .andExpect(jsonPath("$.sessionId").value("session-123"))
                .andExpect(jsonPath("$.tableNumber").value("T01"));
    }
    
    @Test
    @DisplayName("테이블 로그인 API 실패 - 인증 실패")
    void login_Unauthorized() throws Exception {
        // Given
        TableLoginRequestDto request = new TableLoginRequestDto();
        request.setStoreId(1L);
        request.setTableNumber("T01");
        request.setPassword("wrong");
        
        when(authService.login(any(TableLoginRequestDto.class)))
                .thenThrow(new UnauthorizedException(ErrorCode.INVALID_PASSWORD));
        
        // When & Then
        mockMvc.perform(post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("테이블 로그인 API 실패 - 필수 필드 누락")
    void login_ValidationError() throws Exception {
        // Given
        TableLoginRequestDto request = new TableLoginRequestDto();
        // storeId, tableNumber, password 누락
        
        // When & Then
        mockMvc.perform(post("/api/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
