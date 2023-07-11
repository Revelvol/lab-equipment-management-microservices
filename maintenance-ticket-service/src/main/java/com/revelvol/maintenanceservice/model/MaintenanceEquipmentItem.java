package com.revelvol.maintenanceservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maintenance_equipment_items")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MaintenanceEquipmentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String equipmentSkuCode;
    private String description;
    private String maintenanceType;
    private String maintenanceStatus;

//    bidirectional relationship to refer to the maintenance ticket
    @ManyToOne
    @JoinColumn(name = "maintenance_ticket_id")
    private MaintenanceTicket maintenanceTicket;
}
