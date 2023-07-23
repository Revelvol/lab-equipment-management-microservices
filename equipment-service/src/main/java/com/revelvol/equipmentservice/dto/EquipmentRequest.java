package com.revelvol.equipmentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentRequest {
    @NotNull
    private String skuCode;
    private String type;
    private String description;
    private String manufacturer;
    private String model;
    private String serialNumber;
}
