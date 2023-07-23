package com.revelvol.equipmentservice.repository;

import com.revelvol.equipmentservice.model.Equipment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface EquipmentRepository extends MongoRepository<Equipment, String> {
    // Custom method to check if a given skuCode exists in the repository.
    Optional<Equipment> findEquipmentBySkuCode(String skuCode);
}
