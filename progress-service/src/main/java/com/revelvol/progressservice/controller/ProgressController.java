package com.revelvol.progressservice.controller;

import com.revelvol.progressservice.dto.ProgressRequest;
import com.revelvol.progressservice.model.Progress;
import com.revelvol.progressservice.service.ProgressService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progresses")
@AllArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createProgress(@RequestBody ProgressRequest request) {
        progressService.createProgress(request);

        return "Progress successfuly created";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Progress> getAllProgresses(){
        return progressService.getAllProgreses();
    }
}
