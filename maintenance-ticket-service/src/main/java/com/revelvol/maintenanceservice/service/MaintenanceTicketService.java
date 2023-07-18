package com.revelvol.maintenanceservice.service;

import com.revelvol.maintenanceservice.dto.*;
import com.revelvol.maintenanceservice.exception.TicketNotFoundException;
import com.revelvol.maintenanceservice.model.MaintenanceEquipmentItem;
import com.revelvol.maintenanceservice.model.MaintenanceTicket;
import com.revelvol.maintenanceservice.repository.MaintenanceTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // jadi ga perlu autowired
public class MaintenanceTicketService {

    private final MaintenanceTicketRepository maintenanceTicketRepository;


    private MaintenanceEquipmentItem mapRequestEquipmentDtoToMaintenanceEquipmentItem(MaintenanceEquipmentItemsDto maintenanceEquipmentItemsDto, MaintenanceTicket parentTicket) {
        // map the list of MaintenanceEquipment item dto object to maintenance equipment item object and set the parent ticket to the maintenance equipment item
        MaintenanceEquipmentItem maintenanceEquipmentItem = new MaintenanceEquipmentItem();
        maintenanceEquipmentItem.setDescription(maintenanceEquipmentItemsDto.getDescription());
        maintenanceEquipmentItem.setMaintenanceType(maintenanceEquipmentItemsDto.getMaintenanceType());
        //todo implement service communication to the equipment service where it check whether the sku exist or not
        maintenanceEquipmentItem.setEquipmentSkuCode(maintenanceEquipmentItemsDto.getEquipmentSkuCode());
        maintenanceEquipmentItem.setMaintenanceStatus(maintenanceEquipmentItemsDto.getMaintenanceStatus());
        maintenanceEquipmentItem.setMaintenanceTicket(parentTicket);
        return maintenanceEquipmentItem;
    }

    private MaintenanceTicketResponse mapMaintenanceTicket(MaintenanceTicket maintenanceTicket) {

        MaintenanceTicketResponse maintenanceTicketResponse = new MaintenanceTicketResponse();
        maintenanceTicketResponse.setId(maintenanceTicket.getId());
        maintenanceTicketResponse.setTicketNumber(maintenanceTicket.getTicketNumber());
        maintenanceTicketResponse.setDescription(maintenanceTicket.getDescription());
        maintenanceTicketResponse.setIsCompleted(maintenanceTicket.getIsCompleted());

        maintenanceTicketResponse.setMaintenanceEquipmentItems(
                mapMaintenanceEquipmentItemsToMaintenanceEquipmentsItemsDto(maintenanceTicket.getMaintenanceEquipmentItems()));
        return maintenanceTicketResponse;
    }

    private List<MaintenanceEquipmentItemsDtoResponse> mapMaintenanceEquipmentItemsToMaintenanceEquipmentsItemsDto(List<MaintenanceEquipmentItem> equipmentItems) {
        List<MaintenanceEquipmentItemsDtoResponse> maintenanceEquipmentItemsDtoList = new ArrayList<>();
        for (MaintenanceEquipmentItem item : equipmentItems) {
            MaintenanceEquipmentItemsDtoResponse maintenanceEquipmentItemsDto = new MaintenanceEquipmentItemsDtoResponse();
            maintenanceEquipmentItemsDto.setId(item.getId());
            maintenanceEquipmentItemsDto.setDescription(item.getDescription());
            maintenanceEquipmentItemsDto.setEquipmentSkuCode(item.getEquipmentSkuCode());
            maintenanceEquipmentItemsDto.setMaintenanceType(item.getMaintenanceType());
            maintenanceEquipmentItemsDto.setMaintenanceStatus(item.getMaintenanceStatus());

            //append the item to the list
            maintenanceEquipmentItemsDtoList.add(maintenanceEquipmentItemsDto);
        }


        return maintenanceEquipmentItemsDtoList;

    }


    public void createMaintenanceTicket(MaintenanceTicketRequest maintenanceTicketRequest) {
        MaintenanceTicket maintenanceTicket = new MaintenanceTicket();
        maintenanceTicket.setIsCompleted(maintenanceTicketRequest.getIsCompleted());
        // map individual maintenance equipments item dto to create individual maintenance equipment items
        List<MaintenanceEquipmentItem> maintenanceEquipmentItemList = maintenanceTicketRequest.getMaintenanceEquipmentItemsList().stream().map(
                dto -> mapRequestEquipmentDtoToMaintenanceEquipmentItem(dto, maintenanceTicket)).toList();
        //set each maintenance Equipment item to the maintenance ticket
        maintenanceTicket.setMaintenanceEquipmentItems(maintenanceEquipmentItemList);
        maintenanceTicket.setDescription(maintenanceTicketRequest.getDescription());

        maintenanceTicketRepository.save(maintenanceTicket);

    }


    public List<MaintenanceTicketResponse> getAllMaintenanceTickets() {
        List<MaintenanceTicket> maintenanceTicketList = maintenanceTicketRepository.findAll();
        List<MaintenanceTicketResponse> maintenanceTicketResponseList = new ArrayList<>();
        for (MaintenanceTicket ticket : maintenanceTicketList) {
            MaintenanceTicketResponse maintenanceTicketResponse = mapMaintenanceTicket(ticket);
            maintenanceTicketResponseList.add(maintenanceTicketResponse);
        }

        return maintenanceTicketResponseList;
    }

    public MaintenanceTicketResponse getMaintenanceTicketById(Long maintenanceTicketId) {
        MaintenanceTicket maintenanceTicket = getMaintenanceTicketFromId(
                maintenanceTicketId);

        return mapMaintenanceTicket(maintenanceTicket);
    }

    public void deleteMaintenanceTicketById(Long maintenanceTicketId) {

        // todo check whether this need to find the ticket first to not cause 500 error ticket not found
        maintenanceTicketRepository.findById(maintenanceTicketId).orElseThrow(() -> new TicketNotFoundException(
                "Maintenance ticket not found"));
        maintenanceTicketRepository.deleteById(maintenanceTicketId);
    }

    public MaintenanceTicketResponse updateMaintenanceTicketById(Long maintenanceTicketId, MaintenanceTicketRequest maintenanceTicketRequest) {
        MaintenanceTicket curMaintenanceTicket = getMaintenanceTicketFromId(maintenanceTicketId);
        curMaintenanceTicket.setDescription(maintenanceTicketRequest.getDescription());
        curMaintenanceTicket.setIsCompleted(maintenanceTicketRequest.getIsCompleted());

        if (maintenanceTicketRequest.getMaintenanceEquipmentItemsList() != null) {
            List<MaintenanceEquipmentItem> maintenanceEquipmentItemList = maintenanceTicketRequest.getMaintenanceEquipmentItemsList().stream().map(
                    dto -> mapRequestEquipmentDtoToMaintenanceEquipmentItem(dto, curMaintenanceTicket)).collect(
                    Collectors.toList());
            curMaintenanceTicket.setMaintenanceEquipmentItems(maintenanceEquipmentItemList);
        } else {
            curMaintenanceTicket.setMaintenanceEquipmentItems(null);
        }

        MaintenanceTicket updatedMaintenanceTicket = maintenanceTicketRepository.saveAndFlush(curMaintenanceTicket);
        return mapMaintenanceTicket(updatedMaintenanceTicket);
    }

    private MaintenanceTicket getMaintenanceTicketFromId(Long maintenanceTicketId) {
        MaintenanceTicket curMaintenanceTicket = maintenanceTicketRepository.findById(maintenanceTicketId).orElseThrow(() -> new TicketNotFoundException(
                "Maintenance ticket with id: " + maintenanceTicketId + " not found"));
        return curMaintenanceTicket;
    }

    public MaintenanceTicketResponse patchMaintenanceTicketById(Long maintenanceTicketId, UpdateMaintenanceTicketRequest maintenanceTicketRequest) {
        MaintenanceTicket curMaintenanceTicket = getMaintenanceTicketFromId(
                maintenanceTicketId);

        if (maintenanceTicketRequest.getDescription() != null) {
            curMaintenanceTicket.setDescription(maintenanceTicketRequest.getDescription());
        }
        if (maintenanceTicketRequest.getIsCompleted() != null) {
            curMaintenanceTicket.setIsCompleted(maintenanceTicketRequest.getIsCompleted());
        }

        // todo dto not updating
        if (maintenanceTicketRequest.getMaintenanceEquipmentItemsList()!= null) {
            List<MaintenanceEquipmentItem>  maintenanceEquipmentItemList =  maintenanceTicketRequest.getMaintenanceEquipmentItemsList().stream().map(
                    dto -> mapRequestEquipmentDtoToMaintenanceEquipmentItem(dto, curMaintenanceTicket)).collect(
                    Collectors.toList());
            curMaintenanceTicket.setMaintenanceEquipmentItems(maintenanceEquipmentItemList);


        }



        MaintenanceTicket updatedMaintenanceTicket = maintenanceTicketRepository.save(curMaintenanceTicket);
        return mapMaintenanceTicket(updatedMaintenanceTicket);
    }
}
