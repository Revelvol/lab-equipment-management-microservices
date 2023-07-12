package com.revelvol.maintenanceservice.dto;

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
    private Long id;
    @NotNull
    private String equipmentSkuCode;
    private String description;
    private String maintenanceType;
    private String maintenanceStatus="PENDING";
}
