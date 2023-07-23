package com.revelvol.equipmentservice.controller;

import com.revelvol.equipmentservice.dto.EquipmentPatchRequest;
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
    public void createEquipment(@Valid @RequestBody EquipmentRequest productRequest) {
       equipmentService.createEquipment(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EquipmentResponse> getAllEquipments(){
        return equipmentService.getAllEquipments();
    }

    @GetMapping("/{equipment-id}")
    @ResponseStatus(HttpStatus.OK)
    public EquipmentResponse getEquipmentById(@PathVariable("equipment-id") String id) {
        return equipmentService.getEquipmentById(id);
    }

    @GetMapping("/sku/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean findEquipmentExistsBySkuCode(@PathVariable("sku-code") String skuCode) {
        Equipment equipment = equipmentService.findEquipmentBySkuCode(skuCode);

        if (equipment != null) {
            return true;
        }

        return false;
    }



    @PutMapping("/{equipment-id}")
    @ResponseStatus(HttpStatus.OK)
    public EquipmentResponse updateEquipment(@PathVariable("equipment-id") String id, @Valid @RequestBody EquipmentRequest productRequest) {

        return equipmentService.updateEquipment(id, productRequest);
    }

    @PatchMapping("/{equipment-id}")
    @ResponseStatus(HttpStatus.OK)
    public EquipmentResponse patchEquipment(@PathVariable("equipment-id") String id, @Valid @RequestBody EquipmentPatchRequest productRequest) {

        return equipmentService.patchEquipment(id, productRequest);
    }

    @DeleteMapping("/{equipment-id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteEquipment(@PathVariable("equipment-id") String id) {
        equipmentService.deleteEquipment(id);

        return "Equipment deleted successfully";
    }


}
