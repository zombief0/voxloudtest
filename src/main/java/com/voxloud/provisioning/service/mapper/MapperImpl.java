package com.voxloud.provisioning.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.service.models.ConfigurationFile;
import com.voxloud.provisioning.service.models.ConfigurationFileOverride;
import com.voxloud.provisioning.service.models.OverrideFragmentModel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperImpl implements Mapper {
    @Override
    public ConfigurationFile getConfigFileModel(Device device, String domain, String port, String codecs) {
        ConfigurationFile configurationFile = new ConfigurationFile();
        configurationFile.setCodecs(getCodecsList(codecs));
        configurationFile.setPort(port);
        configurationFile.setPassword(device.getPassword());
        configurationFile.setDomain(domain);
        configurationFile.setUsername(device.getUsername());
        return configurationFile;
    }

    @Override
    public String convertToConfigurationJsonString(ConfigurationFile configurationFile) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(configurationFile);
    }

    @Override
    public ConfigurationFileOverride getConfigFileOverride(Device device, String codecs) throws JsonProcessingException {
        ConfigurationFileOverride configurationFileOverride = new ConfigurationFileOverride();
        configurationFileOverride.setCodecs(getCodecsList(codecs));
        configurationFileOverride.setUsername(device.getUsername());
        configurationFileOverride.setPassword(device.getPassword());

        if (device.getModel() == Device.DeviceModel.CONFERENCE) {
            OverrideFragmentModel overrideFragmentModel = new ObjectMapper().readValue(device.getOverrideFragment(), OverrideFragmentModel.class);
            configurationFileOverride.setPort(overrideFragmentModel.getPort());
            configurationFileOverride.setDomain(overrideFragmentModel.getDomain());
            configurationFileOverride.setTimeout(overrideFragmentModel.getTimeout());
        }

        if (device.getModel() == Device.DeviceModel.DESK) {
            String[] overrideFragmentData = device.getOverrideFragment().split("\\n");

            //what we currently have domain=sip.anotherdomain.com
            configurationFileOverride.setDomain(overrideFragmentData[0].split("=")[1]);
            configurationFileOverride.setPort(overrideFragmentData[1].split("=")[1]);

            String timeoutString = overrideFragmentData[2].split("=")[1];
            configurationFileOverride.setTimeout(Integer.parseInt(timeoutString));
        }

        return configurationFileOverride;
    }

    @Override
    public String convertToConfigurationOverrideJsonString(ConfigurationFileOverride configurationFileOverride) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(configurationFileOverride);
    }


    private List<String> getCodecsList(String codecs) {
        String[] codecsArray = codecs.split(",");
        return Arrays.stream(codecsArray).collect(Collectors.toList());
    }
}
