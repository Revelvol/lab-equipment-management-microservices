package com.revelvol.maintenanceservice.dto;

import com.revelvol.maintenanceservice.model.MaintenanceEquipmentItem;
import jakarta.persistence.*;
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
public class MaintenanceTicketResponse {

    private List<MaintenanceEquipmentItemsDtoResponse> maintenanceEquipmentItems;
    private Long id;
    private String ticketNumber;
    private String description;
    private Boolean isCompleted;

}
