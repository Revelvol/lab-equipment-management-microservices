package com.revelvol.progressservice.controller;

import com.revelvol.progressservice.dto.ProgressRequest;
import com.revelvol.progressservice.dto.UpdateProgressRequest;
import com.revelvol.progressservice.model.Progress;
import com.revelvol.progressservice.service.ProgressService;
import jakarta.validation.Valid;
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
    public String createProgress(@RequestBody @Valid ProgressRequest request) {
        progressService.createProgress(request);

        return "Progress successfuly created";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Progress> getAllProgresses(){
        return progressService.getAllProgreses();
    }


    @GetMapping("/{progress-id}")
    @ResponseStatus(HttpStatus.OK)
    public Progress getProgressById(@PathVariable("progress-id") Long progressId){
        return progressService.getProgressById(progressId);
    }

    @PutMapping("/{progress-id}")
    @ResponseStatus(HttpStatus.OK)
    public Progress updateProgress(@PathVariable("progress-id") Long progressId, @RequestBody @Valid UpdateProgressRequest request){
        return progressService.updateProgress(progressId, request);
    }

    @PatchMapping("/{progress-id}")
    @ResponseStatus(HttpStatus.OK)
    public Progress patchProgress(@PathVariable("progress-id") Long progressId, @RequestBody UpdateProgressRequest request){
        return progressService.patchProgress(progressId, request);
    }

    @DeleteMapping("/{progress-id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProgress(@PathVariable("progress-id") Long progressId){
        progressService.deleteProgress(progressId);
        return "Progress-working-order-"+progressId+" successfully deleted";
    }
}
