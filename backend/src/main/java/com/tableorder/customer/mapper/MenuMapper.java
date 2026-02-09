package com.tableorder.customer.mapper;

import com.tableorder.domain.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<Menu> selectMenus(@Param("categoryId") Long categoryId);
    
    Menu selectMenuById(@Param("menuId") Long menuId);
}
