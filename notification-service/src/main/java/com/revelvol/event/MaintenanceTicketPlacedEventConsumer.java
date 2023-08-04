package com.revelvol.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceTicketPlacedEventConsumer {
    private Long id;
    private String ticketNumber;
}
