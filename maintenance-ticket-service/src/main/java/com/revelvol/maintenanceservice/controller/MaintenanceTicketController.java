package com.revelvol.maintenanceservice.controller;

import com.revelvol.maintenanceservice.dto.MaintenanceTicketRequest;
import com.revelvol.maintenanceservice.model.MaintenanceTicket;
import com.revelvol.maintenanceservice.service.MaintenanceTicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/maintenance-ticket")
public class MaintenanceTicketController {

    private final MaintenanceTicketService maintenanceTicketService;

    public MaintenanceTicketController(MaintenanceTicketService maintenanceTicketService) {
        this.maintenanceTicketService = maintenanceTicketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createMaintenanceTicket(@Valid @RequestBody MaintenanceTicketRequest maintenanceTicketRequest) {

        maintenanceTicketService.createMaintenanceTicket(maintenanceTicketRequest);

        return "Maintenance Ticket Successfully created";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MaintenanceTicket> getAllMaintenanceTickets() {
        return maintenanceTicketService.getAllMaintenanceTickets();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/{maintenance-ticket-id}")
    public MaintenanceTicket getMaintenanceTicket(@PathVariable("maintenance-ticket-id") Long maintenanceTicketId) {
        return maintenanceTicketService.getMaintenanceTicketById(maintenanceTicketId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/{maintenance-ticket-id}")
    public MaintenanceTicket updateMaintenanceTicket(@PathVariable("maintenance-ticket-id") Long maintenanceTicketId,
                                                     @Valid @RequestBody MaintenanceTicketRequest maintenanceTicketRequest) {
        return maintenanceTicketService.updateMaintenanceTicketById(maintenanceTicketId,maintenanceTicketRequest);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/{maintenance-ticket-id}")
    public MaintenanceTicket patchMaintenanceTicket(@PathVariable("maintenance-ticket-id") Long maintenanceTicketId,
                                                     @Valid @RequestBody MaintenanceTicketRequest maintenanceTicketRequest) {
        return maintenanceTicketService.patchMaintenanceTicketById(maintenanceTicketId,maintenanceTicketRequest);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/{maintenance-ticket-id}")
    public String updateMaintenanceTicket(@PathVariable("maintenance-ticket-id") Long maintenanceTicketId) {
        maintenanceTicketService.deleteMaintenanceTicketById(maintenanceTicketId);
        return "maintenance-ticket-"+maintenanceTicketId+" successfully deleted";
    }


}
