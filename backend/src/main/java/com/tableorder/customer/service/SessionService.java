package com.tableorder.customer.service;

import com.tableorder.customer.mapper.TableSessionMapper;
import com.tableorder.domain.TableSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final TableSessionMapper tableSessionMapper;
    
    @Transactional
    public TableSession getOrCreateSession(Long tableId) {
        log.info("세션 조회 또는 생성 - tableId: {}", tableId);
        
        // 활성 세션 조회
        TableSession activeSession = tableSessionMapper.selectActiveSession(tableId);
        
        if (activeSession != null) {
            log.info("기존 활성 세션 반환 - sessionId: {}", activeSession.getId());
            return activeSession;
        }
        
        // 새 세션 생성
        TableSession newSession = TableSession.builder()
                
                .tableId(tableId)
                .startedAt(LocalDateTime.now())
                .endedAt(null)
                .isActive(true)
                .build();
        
        tableSessionMapper.insertTableSession(newSession);
        
        log.info("새 세션 생성 완료 - sessionId: {}", newSession.getId());
        return newSession;
    }
    
    @Transactional(readOnly = true)
    public boolean isActive(String sessionId) {
        TableSession session = tableSessionMapper.selectSessionById(sessionId);
        return session != null && session.getIsActive();
    }
}
