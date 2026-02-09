package com.tableorder.customer.controller;

import com.tableorder.customer.dto.CategoryResponseDto;
import com.tableorder.customer.dto.MenuResponseDto;
import com.tableorder.customer.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuService menuService;
    
    @GetMapping("/menus")
    public ResponseEntity<List<MenuResponseDto>> getMenus(
            @RequestParam(required = false) Long categoryId) {
        List<MenuResponseDto> menus = menuService.getMenus(categoryId);
        return ResponseEntity.ok(menus);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDto>> getCategories() {
        List<CategoryResponseDto> categories = menuService.getCategories();
        return ResponseEntity.ok(categories);
    }
}
