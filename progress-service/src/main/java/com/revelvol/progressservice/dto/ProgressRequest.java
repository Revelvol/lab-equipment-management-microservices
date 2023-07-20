package com.revelvol.progressservice.dto;

import com.revelvol.progressservice.model.ProgressDescription;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
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
public class ProgressRequest {
    @NotNull(message = "Sku code is required")
    private String skuCode;
    private String status="ON_GOING";
    private List<ProgressDescriptionDto> progressDescriptionDtoList;
}

