package com.revelvol;


import com.revelvol.event.MaintenanceTicketPlacedEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }


    @KafkaListener(topics = "notificationTopic", groupId = "notificationId")
    public void handleNotification(MaintenanceTicketPlacedEventConsumer maintenanceTicketPlacedEvent) {
        //todo send email notification
        log.info("received notification for "+ maintenanceTicketPlacedEvent.getTicketNumber());
    }
}
