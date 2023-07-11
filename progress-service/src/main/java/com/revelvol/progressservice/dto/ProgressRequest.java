package com.revelvol.progressservice.dto;

import com.revelvol.progressservice.model.ProgressDescription;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProgressRequest {
    private String skuCode;
    private String status;
    private List<ProgressDescriptionDto> progressDescriptionDtoList;
}

