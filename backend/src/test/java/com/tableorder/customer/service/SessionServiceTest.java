package com.tableorder.customer.service;

import com.tableorder.customer.mapper.TableSessionMapper;
import com.tableorder.domain.TableSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
    
    @Mock
    private TableSessionMapper tableSessionMapper;
    
    @InjectMocks
    private SessionService sessionService;
    
    @Test
    @DisplayName("기존 활성 세션 반환")
    void getOrCreateSession_ExistingSession() {
        // Given
        TableSession existingSession = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .startTime(LocalDateTime.now().minusHours(1))
                .isActive(true)
                .build();
        
        when(tableSessionMapper.selectActiveSession(1L)).thenReturn(existingSession);
        
        // When
        TableSession result = sessionService.getOrCreateSession(1L);
        
        // Then
        assertThat(result.getSessionId()).isEqualTo("session-123");
        assertThat(result.getIsActive()).isTrue();
        verify(tableSessionMapper).selectActiveSession(1L);
        verify(tableSessionMapper, never()).insertTableSession(any());
    }
    
    @Test
    @DisplayName("새 세션 생성")
    void getOrCreateSession_NewSession() {
        // Given
        when(tableSessionMapper.selectActiveSession(1L)).thenReturn(null);
        doAnswer(invocation -> {
            TableSession session = invocation.getArgument(0);
            // UUID가 생성되었는지 확인
            assertThat(session.getSessionId()).isNotNull();
            assertThat(session.getTableId()).isEqualTo(1L);
            assertThat(session.getIsActive()).isTrue();
            return null;
        }).when(tableSessionMapper).insertTableSession(any(TableSession.class));
        
        // When
        TableSession result = sessionService.getOrCreateSession(1L);
        
        // Then
        assertThat(result.getSessionId()).isNotNull();
        assertThat(result.getTableId()).isEqualTo(1L);
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getStartTime()).isNotNull();
        verify(tableSessionMapper).selectActiveSession(1L);
        verify(tableSessionMapper).insertTableSession(any(TableSession.class));
    }
    
    @Test
    @DisplayName("세션 활성 상태 확인 - 활성")
    void isActive_ActiveSession() {
        // Given
        TableSession session = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .isActive(true)
                .build();
        
        when(tableSessionMapper.selectSessionById("session-123")).thenReturn(session);
        
        // When
        boolean result = sessionService.isActive("session-123");
        
        // Then
        assertThat(result).isTrue();
        verify(tableSessionMapper).selectSessionById("session-123");
    }
    
    @Test
    @DisplayName("세션 활성 상태 확인 - 비활성")
    void isActive_InactiveSession() {
        // Given
        TableSession session = TableSession.builder()
                .sessionId("session-123")
                .tableId(1L)
                .isActive(false)
                .build();
        
        when(tableSessionMapper.selectSessionById("session-123")).thenReturn(session);
        
        // When
        boolean result = sessionService.isActive("session-123");
        
        // Then
        assertThat(result).isFalse();
        verify(tableSessionMapper).selectSessionById("session-123");
    }
    
    @Test
    @DisplayName("세션 활성 상태 확인 - 세션 없음")
    void isActive_SessionNotFound() {
        // Given
        when(tableSessionMapper.selectSessionById("invalid-session")).thenReturn(null);
        
        // When
        boolean result = sessionService.isActive("invalid-session");
        
        // Then
        assertThat(result).isFalse();
        verify(tableSessionMapper).selectSessionById("invalid-session");
    }
}
