package com.tableorder.customer.service;

import com.tableorder.customer.dto.TableLoginRequestDto;
import com.tableorder.customer.dto.TableLoginResponseDto;
import com.tableorder.customer.mapper.TableMapper;
import com.tableorder.domain.Table;
import com.tableorder.domain.TableSession;
import com.tableorder.exception.ErrorCode;
import com.tableorder.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final TableMapper tableMapper;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public TableLoginResponseDto login(TableLoginRequestDto request) {
        log.info("테이블 로그인 시도 - storeId: {}, tableNumber: {}", 
                request.getStoreId(), request.getTableNumber());
        
        // 1. 테이블 조회
        Table table = tableMapper.selectTableByCredentials(
                request.getStoreId(), 
                request.getTableNumber()
        );
        
        if (table == null) {
            log.warn("테이블을 찾을 수 없음 - storeId: {}, tableNumber: {}", 
                    request.getStoreId(), request.getTableNumber());
            throw new UnauthorizedException(ErrorCode.TABLE_NOT_FOUND);
        }
        
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), table.getPasswordHash())) {
            log.warn("비밀번호 불일치 - tableId: {}", table.getTableId());
            throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
        }
        
        // 3. 세션 생성 또는 조회
        TableSession session = sessionService.getOrCreateSession(table.getTableId());
        
        log.info("테이블 로그인 성공 - tableId: {}, sessionId: {}", 
                table.getTableId(), session.getSessionId());
        
        return new TableLoginResponseDto(
                table.getTableId(),
                session.getSessionId(),
                table.getStoreId(),
                table.getTableNumber()
        );
    }
}
