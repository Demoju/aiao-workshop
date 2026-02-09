package com.tableorder.admin.mapper;

import com.tableorder.domain.TableSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AdminTableSessionMapper {
    Optional<TableSession> findActiveByTableId(@Param("tableId") Long tableId);
    void endSession(@Param("sessionId") Long sessionId, @Param("endedAt") LocalDateTime endedAt);
    List<TableSession> findInactiveByTableId(@Param("tableId") Long tableId, 
                                             @Param("offset") int offset, 
                                             @Param("limit") int limit);
    List<TableSession> findActiveByStoreId(@Param("storeId") Long storeId);
}
