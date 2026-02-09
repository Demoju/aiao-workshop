package com.tableorder.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TableLoginRequestDto {
    @NotNull(message = "매장 ID는 필수입니다")
    private Long storeId;
    
    @NotBlank(message = "테이블 번호는 필수입니다")
    @Size(max = 20, message = "테이블 번호는 20자 이하여야 합니다")
    private String tableNumber;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
