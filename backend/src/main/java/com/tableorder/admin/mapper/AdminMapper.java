package com.tableorder.admin.mapper;

import com.tableorder.domain.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.Optional;

@Mapper
public interface AdminMapper {
    Optional<Admin> findByUsername(@Param("username") String username);
    void updateLoginAttempts(@Param("adminId") Long adminId, 
                            @Param("attempts") Integer attempts, 
                            @Param("lockedUntil") LocalDateTime lockedUntil);
}
