package com.revelvol.apigateway.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {

    private String message;
    private int statusCode;
    private int id;
    private String email;
    private HashMap<Object, Object> data;



}
