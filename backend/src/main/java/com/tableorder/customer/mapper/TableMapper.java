package com.tableorder.customer.mapper;

import com.tableorder.domain.Table;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TableMapper {
    Table selectTableByCredentials(@Param("storeId") Long storeId, 
                                   @Param("tableNumber") String tableNumber);
}
