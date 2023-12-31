package com.revelvol.progressservice.service;

import com.revelvol.progressservice.dto.ProgressDescriptionDto;
import com.revelvol.progressservice.dto.ProgressRequest;
import com.revelvol.progressservice.dto.UpdateProgressRequest;
import com.revelvol.progressservice.exception.ProgressNotFoundException;
import com.revelvol.progressservice.model.Progress;
import com.revelvol.progressservice.model.ProgressDescription;
import com.revelvol.progressservice.repository.ProgressRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;

    public void createProgress(ProgressRequest request) {

        Progress progress = new Progress();
        // sku code must already be exist since it was already checked by the maintenance ticket service
        progress.setSkuCode(request.getSkuCode());
        progress.setStatus(request.getStatus());
        // map the progressDescription Dto list into list of ProgresDescription
        List<ProgressDescription> progressDescriptionList = request.getProgressDescriptionDtoList()
                .stream()
                .map(dto -> mapProgressDescriptionDtoToProgressDescription(dto))
                .toList();

        progress.setProgressDescriptionList(progressDescriptionList);

        progressRepository.save(progress);

    }

    private ProgressDescription mapProgressDescriptionDtoToProgressDescription(ProgressDescriptionDto dto) {
        ProgressDescription progressDescription = new ProgressDescription();
        progressDescription.setDescription(dto.getDescription());
        //set the creation date for the ticket for ordering purpose
        progressDescription.setProgressDate(dto.getProgressDate());
        return progressDescription;
    }

    public List<Progress> getAllProgreses() {
        return progressRepository.findAll();
    }

    public Progress getProgressById(Long progressId) {
        return progressRepository.findById(progressId).orElseThrow(() -> new ProgressNotFoundException(
                "Progress with id:"+progressId+" not found"));
    }

    public Progress updateProgress(Long progressId, UpdateProgressRequest request) {
        Progress curProgress = getProgressById(progressId);

        //todo: decide whether to generate new working order id or not when the process is updated
        curProgress.setSkuCode(request.getSkuCode());
        curProgress.setProgressDescriptionList(request.getProgressDescriptionDtoList()
                .stream()
                .map(this::mapProgressDescriptionDtoToProgressDescription)
                .collect(Collectors.toList()));
        curProgress.setStatus(request.getStatus());
        return progressRepository.saveAndFlush(curProgress);
    }

    public Progress patchProgress(Long progressId, UpdateProgressRequest request) {
        Progress curProgress = getProgressById(progressId);

        //todo: decide whether to generate new working order id or not when the process is updated
        if (request.getSkuCode()!=null) {
            curProgress.setSkuCode(request.getSkuCode());
        }
        if (request.getProgressDescriptionDtoList() != null) {
            curProgress.setProgressDescriptionList(request.getProgressDescriptionDtoList()
                    .stream()
                    .map(this::mapProgressDescriptionDtoToProgressDescription)
                    .collect(Collectors.toList()));
        }
        if (request.getStatus()!=null) {
            curProgress.setStatus(request.getStatus());
        }
        return progressRepository.saveAndFlush(curProgress);
    }

    public void deleteProgress(Long progressId) {
        Progress curProgress = getProgressById(progressId);
        progressRepository.delete(curProgress);
        progressRepository.flush();
    }
}
