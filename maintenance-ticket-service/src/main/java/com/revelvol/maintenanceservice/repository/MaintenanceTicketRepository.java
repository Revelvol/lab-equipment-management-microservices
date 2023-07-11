package com.revelvol.maintenanceservice.repository;

import com.revelvol.maintenanceservice.model.MaintenanceTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceTicketRepository extends JpaRepository<MaintenanceTicket,Long> {
}
