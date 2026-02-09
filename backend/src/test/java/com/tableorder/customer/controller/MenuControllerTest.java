package com.tableorder.customer.controller;

import com.tableorder.customer.dto.CategoryResponseDto;
import com.tableorder.customer.dto.MenuResponseDto;
import com.tableorder.customer.service.MenuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MenuService menuService;
    
    @Test
    @DisplayName("메뉴 조회 API 성공 - 전체 메뉴")
    void getMenus_AllMenus() throws Exception {
        // Given
        MenuResponseDto menu1 = new MenuResponseDto(
                1L, "김치찌개", new BigDecimal("8000"), 
                "얼큰한 김치찌개", "image1.jpg", 1L, true
        );
        MenuResponseDto menu2 = new MenuResponseDto(
                2L, "된장찌개", new BigDecimal("7000"), 
                "구수한 된장찌개", "image2.jpg", 1L, true
        );
        
        List<MenuResponseDto> menus = Arrays.asList(menu1, menu2);
        when(menuService.getMenus(null)).thenReturn(menus);
        
        // When & Then
        mockMvc.perform(get("/api/customer/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].menuName").value("김치찌개"))
                .andExpect(jsonPath("$[1].menuName").value("된장찌개"));
    }
    
    @Test
    @DisplayName("메뉴 조회 API 성공 - 카테고리별")
    void getMenus_ByCategory() throws Exception {
        // Given
        MenuResponseDto menu1 = new MenuResponseDto(
                1L, "김치찌개", new BigDecimal("8000"), 
                "얼큰한 김치찌개", "image1.jpg", 1L, true
        );
        
        List<MenuResponseDto> menus = Arrays.asList(menu1);
        when(menuService.getMenus(1L)).thenReturn(menus);
        
        // When & Then
        mockMvc.perform(get("/api/customer/menus")
                        .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoryId").value(1));
    }
    
    @Test
    @DisplayName("카테고리 조회 API 성공")
    void getCategories_Success() throws Exception {
        // Given
        CategoryResponseDto category1 = new CategoryResponseDto(1L, "찌개류", 1L);
        CategoryResponseDto category2 = new CategoryResponseDto(2L, "면류", 1L);
        
        List<CategoryResponseDto> categories = Arrays.asList(category1, category2);
        when(menuService.getCategories()).thenReturn(categories);
        
        // When & Then
        mockMvc.perform(get("/api/customer/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].categoryName").value("찌개류"))
                .andExpect(jsonPath("$[1].categoryName").value("면류"));
    }
}
