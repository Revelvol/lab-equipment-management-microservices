package com.revelvol.maintenanceservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceTicketPlacedEventProducer {
    private Long id;
    private String ticketNumber;
}
