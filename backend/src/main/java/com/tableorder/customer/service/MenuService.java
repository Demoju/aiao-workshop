package com.tableorder.customer.service;

import com.tableorder.customer.dto.CategoryResponseDto;
import com.tableorder.customer.dto.MenuResponseDto;
import com.tableorder.customer.mapper.CategoryMapper;
import com.tableorder.customer.mapper.MenuMapper;
import com.tableorder.domain.Category;
import com.tableorder.domain.Menu;
import com.tableorder.exception.MenuNotAvailableException;
import com.tableorder.exception.MenuNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {
    
    private final MenuMapper menuMapper;
    private final CategoryMapper categoryMapper;
    
    @Transactional(readOnly = true)
    public List<MenuResponseDto> getMenus(Long categoryId) {
        log.info("메뉴 조회 - categoryId: {}", categoryId);
        
        List<Menu> menus = menuMapper.selectMenus(categoryId);
        
        return menus.stream()
                .map(menu -> new MenuResponseDto(
                        menu.getMenuId(),
                        menu.getMenuName(),
                        menu.getPrice(),
                        menu.getDescription(),
                        menu.getImageUrl(),
                        menu.getCategoryId(),
                        menu.getIsAvailable()
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategories() {
        log.info("카테고리 조회");
        
        List<Category> categories = categoryMapper.selectCategories();
        
        return categories.stream()
                .map(category -> new CategoryResponseDto(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getStoreId()
                ))
                .collect(Collectors.toList());
    }
    
    public void validateMenu(Long menuId) {
        Menu menu = menuMapper.selectMenuById(menuId);
        
        if (menu == null) {
            throw new MenuNotFoundException("메뉴 ID " + menuId + "를 찾을 수 없습니다");
        }
        
        if (!menu.getIsAvailable()) {
            throw new MenuNotAvailableException("품절된 메뉴입니다: " + menu.getMenuName());
        }
    }
}
