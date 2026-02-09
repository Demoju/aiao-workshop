package com.tableorder.customer.service;

import com.tableorder.customer.dto.TableLoginRequestDto;
import com.tableorder.customer.dto.TableLoginResponseDto;
import com.tableorder.customer.mapper.TableMapper;
import com.tableorder.domain.Table;
import com.tableorder.domain.TableSession;
import com.tableorder.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private TableMapper tableMapper;
    
    @Mock
    private SessionService sessionService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    @DisplayName("테이블 로그인 성공")
    void login_Success() {
        // Given
        TableLoginRequestDto request = new TableLoginRequestDto();
        request.setStoreId(1L);
        request.setTableNumber("T01");
        request.setPassword("1234");
        
        Table table = new Table();
        table.setTableId(1L);
        table.setStoreId(1L);
        table.setTableNumber("T01");
        table.setPasswordHash("$2a$10$hashedPassword");
        
        TableSession session = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .isActive(true)
                .build();
        
        when(tableMapper.selectTableByCredentials(1L, "T01")).thenReturn(table);
        when(passwordEncoder.matches("1234", "$2a$10$hashedPassword")).thenReturn(true);
        when(sessionService.getOrCreateSession(1L)).thenReturn(session);
        
        // When
        TableLoginResponseDto response = authService.login(request);
        
        // Then
        assertThat(response.getTableId()).isEqualTo(1L);
        assertThat(response.getSessionId()).isEqualTo("session-123");
        assertThat(response.getTableNumber()).isEqualTo("T01");
        verify(tableMapper).selectTableByCredentials(1L, "T01");
        verify(passwordEncoder).matches("1234", "$2a$10$hashedPassword");
        verify(sessionService).getOrCreateSession(1L);
    }
    
    @Test
    @DisplayName("테이블 로그인 실패 - 테이블 없음")
    void login_TableNotFound() {
        // Given
        TableLoginRequestDto request = new TableLoginRequestDto();
        request.setStoreId(1L);
        request.setTableNumber("T99");
        request.setPassword("1234");
        
        when(tableMapper.selectTableByCredentials(1L, "T99")).thenReturn(null);
        
        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class);
        verify(tableMapper).selectTableByCredentials(1L, "T99");
        verify(passwordEncoder, never()).matches(any(), any());
    }
    
    @Test
    @DisplayName("테이블 로그인 실패 - 비밀번호 불일치")
    void login_InvalidPassword() {
        // Given
        TableLoginRequestDto request = new TableLoginRequestDto();
        request.setStoreId(1L);
        request.setTableNumber("T01");
        request.setPassword("wrong");
        
        Table table = new Table();
        table.setTableId(1L);
        table.setPasswordHash("$2a$10$hashedPassword");
        
        when(tableMapper.selectTableByCredentials(1L, "T01")).thenReturn(table);
        when(passwordEncoder.matches("wrong", "$2a$10$hashedPassword")).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class);
        verify(sessionService, never()).getOrCreateSession(any());
    }
}
