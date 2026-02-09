package com.tableorder.customer.mapper;

import com.tableorder.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository("customerOrderMapper")
public interface OrderMapper {
    void insertOrder(Order order);
    
    List<Order> selectOrdersBySession(@Param("tableId") Long tableId, 
                                      @Param("sessionId") String sessionId);
    
    Order selectOrderById(@Param("orderId") Long orderId);
    
    void deleteOrder(@Param("orderId") Long orderId);
    
    Integer countTodayOrders();
}
