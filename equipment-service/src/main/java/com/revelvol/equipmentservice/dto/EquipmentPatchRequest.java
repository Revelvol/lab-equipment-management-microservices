package com.revelvol.equipmentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentPatchRequest {
    private String name;
    private String type;
    private String description;
    private String manufacturer;
    private String model;
    private String serialNumber;
}
