package com.tableorder.customer.mapper;

import com.tableorder.domain.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    void insertOrderItems(@Param("items") List<OrderItem> items);
    
    List<OrderItem> selectOrderItemsByOrderId(@Param("orderId") Long orderId);
    
    void deleteOrderItems(@Param("orderId") Long orderId);
}
