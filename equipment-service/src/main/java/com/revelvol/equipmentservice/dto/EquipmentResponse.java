package com.revelvol.equipmentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentResponse {
    private String id;
    private String name;
    private String type;
    private String description;
    private String manufacturer;
    private String model;
    private String serialNumber;
}
