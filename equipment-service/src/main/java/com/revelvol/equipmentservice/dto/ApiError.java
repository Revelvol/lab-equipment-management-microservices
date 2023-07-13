package com.revelvol.equipmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private int code;
    private String message;
    private List<String> errorsDetails;
}
