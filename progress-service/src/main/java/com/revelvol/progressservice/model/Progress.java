package com.revelvol.progressservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "progress")
@AllArgsConstructor
@NoArgsConstructor
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String workingOrderId;
    private String skuCode;
    private String status;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ProgressDescription> progressDescriptionList;
}
