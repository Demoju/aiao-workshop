package com.tableorder.customer.mapper;

import com.tableorder.domain.TableSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TableSessionMapper {
    TableSession selectActiveSession(@Param("tableId") Long tableId);
    
    void insertTableSession(TableSession session);
    
    TableSession selectSessionById(@Param("sessionId") String sessionId);
}
