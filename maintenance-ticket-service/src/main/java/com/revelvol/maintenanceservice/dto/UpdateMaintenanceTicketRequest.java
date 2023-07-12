package com.revelvol.maintenanceservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMaintenanceTicketRequest {
    private List<MaintenanceEquipmentItemsDto> maintenanceEquipmentItemsDto;
    private String description;
    private Boolean isCompleted;

}
