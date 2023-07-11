package com.revelvol.progressservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProgressDescriptionDto {
    private Date progressDate;
    private String description;
    // private Image image;
}
