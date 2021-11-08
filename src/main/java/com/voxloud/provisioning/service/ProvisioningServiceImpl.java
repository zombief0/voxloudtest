package com.voxloud.provisioning.service;

import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProvisioningServiceImpl implements ProvisioningService {
    private final DeviceRepository deviceRepository;
    private final Environment environment;

    public String getProvisioningFile(String macAddress) {
        // TODO Implement provisioning
        Optional<Device> optionalDevice = deviceRepository.findById(macAddress);
        if (optionalDevice.isEmpty()){
            return null;
        } else {
            String baseKeyword = "provisioning.";
            Device device = optionalDevice.get();
            return null;
        }
    }
}
