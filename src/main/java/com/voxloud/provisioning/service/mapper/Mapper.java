package com.voxloud.provisioning.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.service.models.ConfigurationFile;
import com.voxloud.provisioning.service.models.ConfigurationFileOverride;

public interface Mapper {
    ConfigurationFile getConfigFileModel(Device device, String domain, String port, String codecs);

    String convertToConfigurationJsonString(ConfigurationFile configurationFile) throws JsonProcessingException;

    ConfigurationFileOverride getConfigFileOverride(Device device,  String codecs) throws JsonProcessingException;

    String convertToConfigurationOverrideJsonString(ConfigurationFileOverride configurationFileOverride) throws JsonProcessingException;
}
