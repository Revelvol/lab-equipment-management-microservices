package com.revelvol.progressservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateProgressRequest {
    @NotNull(message = "Sku code is required")
    private String skuCode;
    private String status;
    private List<ProgressDescriptionDto> progressDescriptionDtoList;
}
