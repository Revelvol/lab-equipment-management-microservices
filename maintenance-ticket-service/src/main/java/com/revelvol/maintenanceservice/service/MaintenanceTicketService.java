package com.revelvol.maintenanceservice.service;

import com.revelvol.maintenanceservice.dto.*;
import com.revelvol.maintenanceservice.event.MaintenanceTicketPlacedEventProducer;
import com.revelvol.maintenanceservice.exception.TicketNotFoundException;
import com.revelvol.maintenanceservice.model.MaintenanceEquipmentItem;
import com.revelvol.maintenanceservice.model.MaintenanceTicket;
import com.revelvol.maintenanceservice.repository.MaintenanceTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // no need to autowired thanks to lombok
@Slf4j
//do i need transactional
public class MaintenanceTicketService {

    private final MaintenanceTicketRepository maintenanceTicketRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, MaintenanceTicketPlacedEventProducer> kafkaTemplate;


    private MaintenanceEquipmentItem mapRequestEquipmentDtoToMaintenanceEquipmentItem(MaintenanceEquipmentItemsDto maintenanceEquipmentItemsDto, MaintenanceTicket parentTicket) {
        // map the list of MaintenanceEquipment item dto object to maintenance equipment item object and set the parent ticket to the maintenance equipment item
        MaintenanceEquipmentItem maintenanceEquipmentItem = new MaintenanceEquipmentItem();
        maintenanceEquipmentItem.setDescription(maintenanceEquipmentItemsDto.getDescription());
        maintenanceEquipmentItem.setMaintenanceType(maintenanceEquipmentItemsDto.getMaintenanceType());
        maintenanceEquipmentItem.setEquipmentSkuCode(maintenanceEquipmentItemsDto.getEquipmentSkuCode());
        maintenanceEquipmentItem.setMaintenanceStatus(maintenanceEquipmentItemsDto.getMaintenanceStatus());
        maintenanceEquipmentItem.setMaintenanceTicket(parentTicket);

        return maintenanceEquipmentItem;

    }

    private void checkEquipmentsItemListSkuCodeIsValid(List<MaintenanceEquipmentItem> equipmentItems) {

        List<String> skuCodeList = equipmentItems.stream().map(MaintenanceEquipmentItem::getEquipmentSkuCode).toList();
        if (!batchCheckIsSkuValid(skuCodeList)) {
            // check if the request sku is valid first
            throw new IllegalArgumentException("One or more sku code is invalid");
        }
    }

    private Boolean batchCheckIsSkuValid(List<String> skuCodeList) {
        // todo: fix webclient config does  not load properly on docker
        // Check tall List of SkuCode is valid and exist on the equipment service
        return webClientBuilder.build().get().uri("http://equipment-service/api/v1/equipments/sku",
                uriBuilder -> uriBuilder.queryParam("skuCodes",
                        skuCodeList).build()).retrieve().bodyToMono(Boolean.class).block();


    }

    private MaintenanceTicketResponse mapMaintenanceTicket(MaintenanceTicket maintenanceTicket) {
        // map maintenance ticket object to maintenance ticket response object
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
        // map equipment items list to the DTO for the request

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


    public String createMaintenanceTicket(MaintenanceTicketRequest maintenanceTicketRequest) {
        MaintenanceTicket maintenanceTicket = new MaintenanceTicket();
        maintenanceTicket.setIsCompleted(maintenanceTicketRequest.getIsCompleted());


        // map individual maintenance equipments item dto to create individual maintenance equipment items
        List<MaintenanceEquipmentItem> maintenanceEquipmentItemList = maintenanceTicketRequest.getMaintenanceEquipmentItemsList().stream().map(
                dto -> mapRequestEquipmentDtoToMaintenanceEquipmentItem(dto, maintenanceTicket)).toList();

        //check the list of maintenance equipment item have valid sku code

        checkEquipmentsItemListSkuCodeIsValid(maintenanceEquipmentItemList);

        //set each maintenance Equipment item to the maintenance ticket
        maintenanceTicket.setMaintenanceEquipmentItems(maintenanceEquipmentItemList);
        maintenanceTicket.setDescription(maintenanceTicketRequest.getDescription());

        maintenanceTicketRepository.save(maintenanceTicket);
        //send the maintenance ticket to the kafka topic asynchronusly
        kafkaTemplate.send("notificationTopic",
                new MaintenanceTicketPlacedEventProducer(maintenanceTicket.getId(),
                        maintenanceTicket.getTicketNumber()));


        log.info("maintenance ticker sucessfuly created: " + maintenanceTicket.getTicketNumber());
        return "maintenance ticket " + maintenanceTicket.getTicketNumber() + " saved sucessfully";

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
        MaintenanceTicket maintenanceTicket = getMaintenanceTicketFromId(maintenanceTicketId);

        return mapMaintenanceTicket(maintenanceTicket);
    }

    public void deleteMaintenanceTicketById(Long maintenanceTicketId) {
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
            checkEquipmentsItemListSkuCodeIsValid(maintenanceEquipmentItemList);
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
        MaintenanceTicket curMaintenanceTicket = getMaintenanceTicketFromId(maintenanceTicketId);

        if (maintenanceTicketRequest.getDescription() != null) {
            curMaintenanceTicket.setDescription(maintenanceTicketRequest.getDescription());
        }
        if (maintenanceTicketRequest.getIsCompleted() != null) {
            curMaintenanceTicket.setIsCompleted(maintenanceTicketRequest.getIsCompleted());
        }

        if (maintenanceTicketRequest.getMaintenanceEquipmentItemsList() != null) {
            List<MaintenanceEquipmentItem> maintenanceEquipmentItemList = maintenanceTicketRequest.getMaintenanceEquipmentItemsList().stream().map(
                    dto -> mapRequestEquipmentDtoToMaintenanceEquipmentItem(dto, curMaintenanceTicket)).collect(
                    Collectors.toList());
            checkEquipmentsItemListSkuCodeIsValid(maintenanceEquipmentItemList);
            curMaintenanceTicket.setMaintenanceEquipmentItems(maintenanceEquipmentItemList);


        }


        MaintenanceTicket updatedMaintenanceTicket = maintenanceTicketRepository.save(curMaintenanceTicket);
        return mapMaintenanceTicket(updatedMaintenanceTicket);
    }
}
