package com.tableorder.customer.service;

import com.tableorder.customer.dto.MenuResponseDto;
import com.tableorder.customer.mapper.MenuMapper;
import com.tableorder.domain.Menu;
import com.tableorder.exception.MenuNotAvailableException;
import com.tableorder.exception.MenuNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    
    @Mock
    private MenuMapper menuMapper;
    
    @InjectMocks
    private MenuService menuService;
    
    @Test
    @DisplayName("메뉴 조회 성공")
    void getMenus_Success() {
        // Given
        Menu menu1 = new Menu();
        menu1.setMenuId(1L);
        menu1.setMenuName("김치찌개");
        menu1.setPrice(new BigDecimal("8000"));
        menu1.setIsAvailable(true);
        
        Menu menu2 = new Menu();
        menu2.setMenuId(2L);
        menu2.setMenuName("된장찌개");
        menu2.setPrice(new BigDecimal("7000"));
        menu2.setIsAvailable(true);
        
        when(menuMapper.selectMenus(null)).thenReturn(Arrays.asList(menu1, menu2));
        
        // When
        List<MenuResponseDto> result = menuService.getMenus(null);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMenuName()).isEqualTo("김치찌개");
        assertThat(result.get(1).getMenuName()).isEqualTo("된장찌개");
        verify(menuMapper).selectMenus(null);
    }
    
    @Test
    @DisplayName("메뉴 검증 성공")
    void validateMenu_Success() {
        // Given
        Menu menu = new Menu();
        menu.setMenuId(1L);
        menu.setMenuName("김치찌개");
        menu.setIsAvailable(true);
        
        when(menuMapper.selectMenuById(1L)).thenReturn(menu);
        
        // When & Then
        assertThatCode(() -> menuService.validateMenu(1L))
                .doesNotThrowAnyException();
        verify(menuMapper).selectMenuById(1L);
    }
    
    @Test
    @DisplayName("메뉴 검증 실패 - 메뉴 없음")
    void validateMenu_MenuNotFound() {
        // Given
        when(menuMapper.selectMenuById(999L)).thenReturn(null);
        
        // When & Then
        assertThatThrownBy(() -> menuService.validateMenu(999L))
                .isInstanceOf(MenuNotFoundException.class);
    }
    
    @Test
    @DisplayName("메뉴 검증 실패 - 품절")
    void validateMenu_MenuNotAvailable() {
        // Given
        Menu menu = new Menu();
        menu.setMenuId(1L);
        menu.setMenuName("냉면");
        menu.setIsAvailable(false);
        
        when(menuMapper.selectMenuById(1L)).thenReturn(menu);
        
        // When & Then
        assertThatThrownBy(() -> menuService.validateMenu(1L))
                .isInstanceOf(MenuNotAvailableException.class);
    }
}
