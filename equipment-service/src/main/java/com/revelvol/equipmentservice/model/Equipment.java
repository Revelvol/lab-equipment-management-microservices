package com.revelvol.equipmentservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "equipment")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Equipment {
    @Id
    private String id;
    private String name;
    private String type;
    private String description;
    private String manufacturer;
    private String model;
    private String serialNumber;
}
