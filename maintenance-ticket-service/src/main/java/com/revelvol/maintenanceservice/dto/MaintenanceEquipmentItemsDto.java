package com.revelvol.maintenanceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaintenanceEquipmentItemsDto {
    @NotEmpty(message = "Equipment Sku code is required")
    private String equipmentSkuCode;
    private String description;
    private String maintenanceType;
    private String maintenanceStatus="PENDING";
}
