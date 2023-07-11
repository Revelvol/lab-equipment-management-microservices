package com.revelvol.equipmentservice.repository;

import com.revelvol.equipmentservice.model.Equipment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EquipmentRepository extends MongoRepository<Equipment, String> {
}
