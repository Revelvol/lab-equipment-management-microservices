package com.revelvol.maintenanceservice.controller;

import com.revelvol.maintenanceservice.dto.ApiError;
import com.revelvol.maintenanceservice.dto.MaintenanceTicketRequest;
import com.revelvol.maintenanceservice.dto.MaintenanceTicketResponse;
import com.revelvol.maintenanceservice.dto.UpdateMaintenanceTicketRequest;
import com.revelvol.maintenanceservice.service.MaintenanceTicketService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/maintenance-ticket")
public class MaintenanceTicketController {

    private final MaintenanceTicketService maintenanceTicketService;

    public MaintenanceTicketController(MaintenanceTicketService maintenanceTicketService) {
        this.maintenanceTicketService = maintenanceTicketService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //add circuit breaker here because this make call with another service
    @CircuitBreaker(name = "equipment", fallbackMethod = "fallbackCreateMaintenanceTicket")
    @TimeLimiter(name = "equipment") // karna ada timeout jadi call mesti made into asynchronus
    public CompletableFuture<String> createMaintenanceTicket(@Valid @RequestBody MaintenanceTicketRequest maintenanceTicketRequest) {

        return CompletableFuture.supplyAsync(() -> maintenanceTicketService.createMaintenanceTicket(
                maintenanceTicketRequest));
    }


    //function that are executed if the circuit fails
    public CompletableFuture<String> fallbackCreateMaintenanceTicket(MaintenanceTicketRequest maintenanceTicketRequest, RuntimeException e) {

        return CompletableFuture.supplyAsync(() -> "something went wrong");
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MaintenanceTicketResponse> getAllMaintenanceTickets() {
        return maintenanceTicketService.getAllMaintenanceTickets();
    }

    @GetMapping("/{maintenance-ticket-id}")
    @ResponseStatus(HttpStatus.OK)
    public MaintenanceTicketResponse getMaintenanceTicket(@PathVariable("maintenance-ticket-id") Long maintenanceTicketId) {
        return maintenanceTicketService.getMaintenanceTicketById(maintenanceTicketId);
    }

    @PutMapping("/{maintenance-ticket-id}")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "equipment", fallbackMethod = "fallbackUpdateMaintenanceTicket")
    @TimeLimiter(name = "equipment")
    public CompletableFuture<MaintenanceTicketResponse> updateMaintenanceTicket(@PathVariable("maintenance-ticket-id") Long maintenanceTicketId,
                                                                                @Valid @RequestBody MaintenanceTicketRequest maintenanceTicketRequest) {

        return CompletableFuture.supplyAsync(() -> maintenanceTicketService.updateMaintenanceTicketById(
                maintenanceTicketId,
                maintenanceTicketRequest));
    }


    //temporary fallback method since now the server cannot throw exception and be catched
    public CompletableFuture<MaintenanceTicketResponse> fallbackUpdateMaintenanceTicket(
            Long maintenanceTicketId, MaintenanceTicketRequest maintenanceTicketRequest) {


        // todo implement caching here
        return CompletableFuture.supplyAsync(()-> {
            MaintenanceTicketResponse errorResponse = new MaintenanceTicketResponse();
            errorResponse.setDescription("Something went wrong while updating the maintenance ticket.");
            return errorResponse;
        });
    }


    @PatchMapping("/{maintenance-ticket-id}")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "equipment", fallbackMethod = "fallbackPatchMaintenanceTicket")
    @TimeLimiter(name = "equipment")
    public CompletableFuture<MaintenanceTicketResponse> patchMaintenanceTicket(@PathVariable("maintenance-ticket-id") Long maintenanceTicketId,
                                                                               @Valid @RequestBody UpdateMaintenanceTicketRequest maintenanceTicketRequest) {
        return CompletableFuture.supplyAsync(() -> maintenanceTicketService.patchMaintenanceTicketById(
                maintenanceTicketId,
                maintenanceTicketRequest));

    }

    //temporary fallback method since now the server cannot throw exception and be catched
    public CompletableFuture<MaintenanceTicketResponse> fallbackPatchMaintenanceTicket(
            Long maintenanceTicketId, MaintenanceTicketRequest maintenanceTicketRequest) {


        // todo implement caching here
        return CompletableFuture.supplyAsync(()-> {
            MaintenanceTicketResponse errorResponse = new MaintenanceTicketResponse();
            errorResponse.setDescription("Something went wrong while updating the maintenance ticket.");
            return errorResponse;
        });
    }
    @DeleteMapping("/{maintenance-ticket-id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateMaintenanceTicket(@PathVariable("maintenance-ticket-id") Long maintenanceTicketId) {
        maintenanceTicketService.deleteMaintenanceTicketById(maintenanceTicketId);
        return "maintenance-ticket-" + maintenanceTicketId + " successfully deleted";
    }


}
