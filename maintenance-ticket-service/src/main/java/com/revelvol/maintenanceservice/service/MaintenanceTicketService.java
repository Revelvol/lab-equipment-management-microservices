package com.revelvol.maintenanceservice.service;

import com.revelvol.maintenanceservice.dto.MaintenanceEquipmentItemsDto;
import com.revelvol.maintenanceservice.dto.MaintenanceTicketRequest;
import com.revelvol.maintenanceservice.dto.UpdateMaintenanceTicketRequest;
import com.revelvol.maintenanceservice.exception.TicketNotFoundException;
import com.revelvol.maintenanceservice.model.MaintenanceEquipmentItem;
import com.revelvol.maintenanceservice.model.MaintenanceTicket;
import com.revelvol.maintenanceservice.repository.MaintenanceTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // jadi ga perlu autowired
public class MaintenanceTicketService {

    private final MaintenanceTicketRepository maintenanceTicketRepository;


    private MaintenanceEquipmentItem mapRequestEquipmentDtoToMaintenanceEquipmentItem(MaintenanceEquipmentItemsDto maintenanceEquipmentItemsDto, MaintenanceTicket parentTicket) {
        // map the list of MaintenanceEquipment item dto object to maintenance equipment item object and set the parent ticket to the maintenance equipment item
        MaintenanceEquipmentItem maintenanceEquipmentItem = new MaintenanceEquipmentItem();
        maintenanceEquipmentItem.setDescription(maintenanceEquipmentItemsDto.getDescription());
        maintenanceEquipmentItem.setMaintenanceType(maintenanceEquipmentItem.getMaintenanceType());
        //todo implement service communication to the equipment service where it check whether the sku exist or not
        maintenanceEquipmentItem.setEquipmentSkuCode(maintenanceEquipmentItem.getEquipmentSkuCode());
        maintenanceEquipmentItem.setMaintenanceStatus(maintenanceEquipmentItem.getMaintenanceStatus());
        maintenanceEquipmentItem.setMaintenanceTicket(parentTicket);
        return maintenanceEquipmentItem;
    }

    public void createMaintenanceTicket(MaintenanceTicketRequest maintenanceTicketRequest) {
        MaintenanceTicket maintenanceTicket = new MaintenanceTicket();
        //create random ticket number uuid
        maintenanceTicket.setTicketNumber("MT_" + UUID.randomUUID());
        maintenanceTicket.setIsCompleted(maintenanceTicketRequest.getIsCompleted());
        // map individual maintenance equipments item dto to create individual maintenance equipment items
        List<MaintenanceEquipmentItem> maintenanceEquipmentItemList = maintenanceTicketRequest.getMaintenanceEquipmentItemsDto().stream().map(
                dto -> mapRequestEquipmentDtoToMaintenanceEquipmentItem(dto, maintenanceTicket)).toList();
        //set each maintenance Equipment item to the maintenance ticket
        maintenanceTicket.setMaintenanceEquipmentItems(maintenanceEquipmentItemList);
        maintenanceTicket.setDescription(maintenanceTicketRequest.getDescription());

        maintenanceTicketRepository.save(maintenanceTicket);

    }


    public List<MaintenanceTicket> getAllMaintenanceTickets() {
        return maintenanceTicketRepository.findAll();
    }

    public MaintenanceTicket getMaintenanceTicketById(Long maintenanceTicketId) {
        return maintenanceTicketRepository.findById(maintenanceTicketId).orElseThrow(() -> new TicketNotFoundException(
                "Maintenance ticket not found"));
    }

    public void deleteMaintenanceTicketById(Long maintenanceTicketId) {

        // todo check whether this need to find the ticket first to not cause 500 error ticket not found
        maintenanceTicketRepository.findById(maintenanceTicketId).orElseThrow(() -> new TicketNotFoundException(
                "Maintenance ticket not found"));
        maintenanceTicketRepository.deleteById(maintenanceTicketId);
    }

    public MaintenanceTicket updateMaintenanceTicketById(Long maintenanceTicketId, UpdateMaintenanceTicketRequest maintenanceTicketRequest) {
        MaintenanceTicket curMaintenanceTicket = maintenanceTicketRepository.findById(maintenanceTicketId).orElseThrow(() -> new TicketNotFoundException(
                "Maintenance ticket not found"));
        curMaintenanceTicket.setDescription(maintenanceTicketRequest.getDescription());
        curMaintenanceTicket.setIsCompleted(maintenanceTicketRequest.getIsCompleted());
        List<MaintenanceEquipmentItem> maintenanceEquipmentItemList = maintenanceTicketRequest.getMaintenanceEquipmentItemsDto().stream().map(
                dto -> mapRequestEquipmentDtoToMaintenanceEquipmentItem(dto, curMaintenanceTicket)).toList();

        curMaintenanceTicket.setMaintenanceEquipmentItems(maintenanceEquipmentItemList);

        return maintenanceTicketRepository.saveAndFlush(curMaintenanceTicket);
    }

    public MaintenanceTicket patchMaintenanceTicketById(Long maintenanceTicketId, UpdateMaintenanceTicketRequest maintenanceTicketRequest) {
        MaintenanceTicket curMaintenanceTicket = maintenanceTicketRepository.findById(maintenanceTicketId).orElseThrow(() -> new TicketNotFoundException(
                "Maintenance ticket not found"));

        if (!maintenanceTicketRequest.getDescription().isEmpty()) {
            curMaintenanceTicket.setDescription(maintenanceTicketRequest.getDescription());
        }
        if (maintenanceTicketRequest.getIsCompleted() != null) {
            curMaintenanceTicket.setIsCompleted(maintenanceTicketRequest.getIsCompleted());
        }

        List<MaintenanceEquipmentItem> maintenanceEquipmentItemList = maintenanceTicketRequest.getMaintenanceEquipmentItemsDto().isEmpty() ? null : maintenanceTicketRequest.getMaintenanceEquipmentItemsDto().stream().map(
                dto -> mapRequestEquipmentDtoToMaintenanceEquipmentItem(dto, curMaintenanceTicket)).toList();

        curMaintenanceTicket.setMaintenanceEquipmentItems(maintenanceEquipmentItemList);

        return maintenanceTicketRepository.saveAndFlush(curMaintenanceTicket);
    }
}
