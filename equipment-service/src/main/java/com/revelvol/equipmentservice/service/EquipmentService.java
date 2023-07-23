package com.revelvol.equipmentservice.service;

import com.revelvol.equipmentservice.dto.EquipmentPatchRequest;
import com.revelvol.equipmentservice.dto.EquipmentRequest;
import com.revelvol.equipmentservice.dto.EquipmentResponse;
import com.revelvol.equipmentservice.exception.EquipmentNotFoundException;
import com.revelvol.equipmentservice.model.Equipment;
import com.revelvol.equipmentservice.repository.EquipmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j //add logs from llombok
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public void createEquipment(EquipmentRequest equipmentRequest) {
        Equipment equipment = Equipment.builder()
                .skuCode(equipmentRequest.getSkuCode())
                .description(equipmentRequest.getDescription())
                .type(equipmentRequest.getType())
                .manufacturer(equipmentRequest.getManufacturer())
                .serialNumber(equipmentRequest.getSerialNumber())
                .build();
        // todo handle error handling duplicate sku code here
        equipmentRepository.save(equipment);
        log.info("Equipment {} is saved", equipment.getId());
    }

    public List<EquipmentResponse> getAllEquipments() {
        List<Equipment> equipments = equipmentRepository.findAll();

        return equipments.stream().map(this::mapToEquipmentResponse).toList();
    }

    private EquipmentResponse mapToEquipmentResponse(Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .skuCode(equipment.getSkuCode())
                .description(equipment.getDescription())
                .type(equipment.getType())
                .manufacturer(equipment.getManufacturer())
                .serialNumber(equipment.getSerialNumber())
                .model(equipment.getModel())
                .build();

    }

    public EquipmentResponse getEquipmentById(String id) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new EquipmentNotFoundException("Equipment not found"));
        return mapToEquipmentResponse(equipment);
    }

    public EquipmentResponse updateEquipment(String id, EquipmentRequest productRequest) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new EquipmentNotFoundException("Equipment not found"));
        equipment.setSkuCode(productRequest.getSkuCode());
        equipment.setDescription(productRequest.getDescription());
        equipment.setType(productRequest.getType());
        equipment.setManufacturer(productRequest.getManufacturer());
        equipment.setSerialNumber(productRequest.getSerialNumber());
        equipment.setModel(productRequest.getModel());
        equipmentRepository.save(equipment);
        return mapToEquipmentResponse(equipment);
    }

    public EquipmentResponse patchEquipment(String id, EquipmentPatchRequest productRequest) {

        Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new EquipmentNotFoundException("Equipment not found"));
        if (productRequest.getName() != null) {
            equipment.setSkuCode(productRequest.getName());
        }
        if (productRequest.getDescription() != null) {
            equipment.setDescription(productRequest.getDescription());
        }
        if (productRequest.getType() != null) {
            equipment.setType(productRequest.getType());
        }
        if (productRequest.getManufacturer() != null) {
            equipment.setManufacturer(productRequest.getManufacturer());
        }
        if (productRequest.getSerialNumber() != null) {
            equipment.setSerialNumber(productRequest.getSerialNumber());
        }

        if (productRequest.getModel() != null) {
            equipment.setModel(productRequest.getModel());
        }
        equipmentRepository.save(equipment);
        return mapToEquipmentResponse(equipment);
    }

    public void deleteEquipment(String id) {
        equipmentRepository.findById(id).orElseThrow(() -> new EquipmentNotFoundException("Equipment not found"));
        equipmentRepository.deleteById(id);
    }

    public Equipment findEquipmentBySkuCode(String skuCode) {
        // return equipment or throw and error not found exception
        return equipmentRepository.findEquipmentBySkuCode(skuCode).orElseThrow(() -> new EquipmentNotFoundException("Equipment not found"));
    }

    public Boolean isEquipmentExistsBySkuCode(String skuCode) {
        // return true or false according to the sku code given in the request path variable
        return equipmentRepository.findEquipmentBySkuCode(skuCode).isPresent();
    }

    public Boolean isEquipmentExistsBySkuCodeBatch(List<String> skuCodes) {
        // return true or false according to the sku codes given in the request params
        // todo consider responding more information of which sku codes is invalid
        for (String skuCode : skuCodes) {
            if (!isEquipmentExistsBySkuCode(skuCode)) {
                return false;
            }
        }
        return true;
    }
}
