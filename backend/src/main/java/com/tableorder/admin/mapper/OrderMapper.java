package com.tableorder.admin.mapper;

import com.tableorder.domain.Order;
import com.tableorder.domain.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {
    List<Order> findBySessionIdIn(@Param("sessionIds") List<Long> sessionIds);
    Optional<Order> findById(@Param("orderId") Long orderId);
    void updateStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);
    void deleteById(@Param("orderId") Long orderId);
}
