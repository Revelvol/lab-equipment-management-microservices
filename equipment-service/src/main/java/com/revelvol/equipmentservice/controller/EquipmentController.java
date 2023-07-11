package com.revelvol.equipmentservice.controller;

import com.revelvol.equipmentservice.dto.EquipmentRequest;
import com.revelvol.equipmentservice.dto.EquipmentResponse;
import com.revelvol.equipmentservice.model.Equipment;
import com.revelvol.equipmentservice.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipments")
@AllArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createEquipment(@RequestBody EquipmentRequest productRequest) {
       equipmentService.createEquipment(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EquipmentResponse> getAllEquipments(){
        return equipmentService.getAllEquipments();
    }



}
