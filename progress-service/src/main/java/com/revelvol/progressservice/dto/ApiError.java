package com.revelvol.progressservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApiError {
    private Integer code; // kind of redundant karena http structure sudah bawa code
    private String message;
    private List<String> errorsDetails;
}
