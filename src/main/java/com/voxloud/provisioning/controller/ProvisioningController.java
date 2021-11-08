package com.voxloud.provisioning.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voxloud.provisioning.service.ProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProvisioningController {
    private final ProvisioningService provisioningService;

    // TODO Implement controller method
    @GetMapping("/provisioning/{macAddress}")
    public ResponseEntity<?> getConfig(@PathVariable String macAddress) throws JsonProcessingException {
        String provisioningFile = provisioningService.getProvisioningFile(macAddress);
        if (provisioningFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(provisioningFile);

    }
}
