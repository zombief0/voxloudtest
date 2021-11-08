package com.voxloud.provisioning.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.service.mapper.Mapper;
import com.voxloud.provisioning.service.models.ConfigurationFile;
import com.voxloud.provisioning.service.models.ConfigurationFileOverride;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProvisioningServiceImpl implements ProvisioningService {
    private final DeviceRepository deviceRepository;
    private final Environment environment;
    private final Mapper mapper;

    public String getProvisioningFile(String macAddress) throws JsonProcessingException {
        // TODO Implement provisioning
        Optional<Device> optionalDevice = deviceRepository.findById(macAddress);
        if (optionalDevice.isEmpty()) {
            return null;
        }

        Device device = optionalDevice.get();
        String baseKeyword = "provisioning.";
        if (device.getOverrideFragment() == null) {
            ConfigurationFile configFileModel = mapper.getConfigFileModel(device, environment.getProperty(baseKeyword + "domain"), environment.getProperty(baseKeyword + "port"), environment.getProperty(baseKeyword + "codecs"));
            return getConfigFormatNoOverride(device.getModel(), configFileModel);
        }

        ConfigurationFileOverride configurationFileOverride = mapper.getConfigFileOverride(device, environment.getProperty(baseKeyword + "codecs"));
        return getConfigFormatOverride(device.getModel(), configurationFileOverride);
    }

    private String getConfigFormatNoOverride(Device.DeviceModel deviceModel, ConfigurationFile configFileModel) throws JsonProcessingException {
        switch (deviceModel.toString()) {
            case "DESK":
                return createConfigPlainText(configFileModel);

            case "CONFERENCE":
                return mapper.convertToConfigurationJsonString(configFileModel);

            default:
                return createConfigPlainText(configFileModel);
        }
    }

    private String getConfigFormatOverride(Device.DeviceModel deviceModel, ConfigurationFileOverride configurationFileOverride) throws JsonProcessingException {
        switch (deviceModel.toString()) {
            case "DESK":
                return createConfigOverridePlainText(configurationFileOverride);

            case "CONFERENCE":
                return mapper.convertToConfigurationOverrideJsonString(configurationFileOverride);

            default:
                return createConfigOverridePlainText(configurationFileOverride);
        }
    }

    private String createConfigPlainText(ConfigurationFile configurationFile) {
        StringBuilder configPlainTextBuilder = setCommonConfig(configurationFile.getUsername(), configurationFile.getPassword(), configurationFile.getDomain(), configurationFile.getPort(), configurationFile.getCodecs());
        String configPlainText;
        configPlainText = configPlainTextBuilder.toString();
        int indexOfLastComa = configPlainText.lastIndexOf(",");

        return configPlainText.substring(0, indexOfLastComa);
    }

    private String createConfigOverridePlainText(ConfigurationFileOverride configurationFileOverride) {
        StringBuilder configPlainTextBuilder = setCommonConfig(configurationFileOverride.getUsername(), configurationFileOverride.getPassword(), configurationFileOverride.getDomain(), configurationFileOverride.getPort(), configurationFileOverride.getCodecs());
        String configPlainText;
        configPlainTextBuilder.append("\ntimeout=").append(configurationFileOverride.getTimeout());
        configPlainText = configPlainTextBuilder.toString();
        int indexOfLastComa = configPlainText.lastIndexOf(",");
        if (indexOfLastComa == -1) {
            return configPlainText;
        }
        return configPlainText.substring(0, indexOfLastComa) + configPlainText.substring(indexOfLastComa + 1);
    }

    private StringBuilder setCommonConfig(String username, String password, String domain, String port, List<String> codecs) {
        StringBuilder configPlainTextBuilder = new StringBuilder();
        configPlainTextBuilder.append("username=").append(username).append("\n");
        configPlainTextBuilder.append("password=").append(password).append("\n");
        configPlainTextBuilder.append("domain=").append(domain).append("\n");
        configPlainTextBuilder.append("port=").append(port).append("\n");
        configPlainTextBuilder.append("codecs=");
        if (!codecs.isEmpty()) {
            codecs.forEach(codec -> configPlainTextBuilder.append(codec).append(","));
        }
        return configPlainTextBuilder;
    }
}
