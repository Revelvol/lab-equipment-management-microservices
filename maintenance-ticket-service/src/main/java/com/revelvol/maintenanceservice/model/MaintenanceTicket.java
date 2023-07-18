package com.revelvol.maintenanceservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "maintenance_ticket")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MaintenanceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticketNumber = "MT_" + UUID.randomUUID();
    @OneToMany( mappedBy ="maintenanceTicket", cascade = CascadeType.ALL) // mapped by represent the parent for the child to access the parent, kalau ga mau remove the mappedBy
    private List<MaintenanceEquipmentItem> maintenanceEquipmentItems;
    private String description;
    private Boolean isCompleted = Boolean.FALSE;
}
