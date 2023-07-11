package com.revelvol.maintenanceservice.dto;


import com.revelvol.maintenanceservice.model.MaintenanceEquipmentItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class MaintenanceTicketRequest {
    @NotNull
    private List<MaintenanceEquipmentItemsDto> maintenanceEquipmentItemsDto;
    private String description;
    private Boolean isCompleted;

}
