package com.revelvol.equipmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentResponse {
    private String id;
    private String skuCode;
    private String type;
    private String description;
    private String manufacturer;
    private String model;
    private String serialNumber;
}
