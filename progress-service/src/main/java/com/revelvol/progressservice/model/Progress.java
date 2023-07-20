package com.revelvol.progressservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "progress")
@AllArgsConstructor
@NoArgsConstructor
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String workingOrderId="WO_" + UUID.randomUUID();
    private String skuCode;
    private String status="IN PROGRESS";
    @OneToMany(cascade = CascadeType.ALL)
    private List<ProgressDescription> progressDescriptionList;
}
