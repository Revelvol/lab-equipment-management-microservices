package com.revelvol.progressservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Table(name = "progress_description")
@AllArgsConstructor
@NoArgsConstructor
public class ProgressDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date progressDate;
    private String description;
    // private Image image;
}
