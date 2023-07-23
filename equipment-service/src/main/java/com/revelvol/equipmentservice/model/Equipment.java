package com.revelvol.equipmentservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "equipment")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Equipment {
    @Id
    private String id;
    @Indexed(unique = true, background = true)
    private String skuCode; // the name of the item
    private String type;
    private String description;
    private String manufacturer;
    private String model;
    private String serialNumber;
}
