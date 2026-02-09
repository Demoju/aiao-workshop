package com.tableorder.customer.mapper;

import com.tableorder.domain.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> selectCategories();
}
