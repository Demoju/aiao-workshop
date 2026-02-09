package com.tableorder.customer.controller;

import com.tableorder.customer.dto.TableLoginRequestDto;
import com.tableorder.customer.dto.TableLoginResponseDto;
import com.tableorder.customer.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<TableLoginResponseDto> login(@RequestBody @Valid TableLoginRequestDto request) {
        TableLoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
