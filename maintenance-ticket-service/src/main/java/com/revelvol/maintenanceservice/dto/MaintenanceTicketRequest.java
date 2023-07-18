package com.revelvol.maintenanceservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaintenanceTicketRequest {
    @NotEmpty(message = "Maintenance Equipment Items List is required")
    @Valid
    private List<MaintenanceEquipmentItemsDto> maintenanceEquipmentItemsList;
    private String description;
    private Boolean isCompleted = Boolean.FALSE;

}
