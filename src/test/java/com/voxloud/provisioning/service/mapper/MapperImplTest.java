package com.voxloud.provisioning.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.service.models.ConfigurationFile;
import com.voxloud.provisioning.service.models.ConfigurationFileOverride;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MapperImplTest {

    @InjectMocks
    private MapperImpl mapper;


    @Test
    void getConfigFileModel() {
        Device device = new Device();
        device.setMacAddress("aa-bb-cc-dd-ee-ff");
        device.setOverrideFragment(null);
        device.setPassword("doe");
        device.setUsername("john");

        ConfigurationFile configFileModel = mapper.getConfigFileModel(device, "domain", "6096", "6fe,745Fr,258We");

        assertThat(configFileModel).isNotNull();
    }

    @Test
    void convertToConfigurationJsonString() throws JsonProcessingException {
        String jsonStringConfig = mapper.convertToConfigurationJsonString(new ConfigurationFile());

        assertThat(jsonStringConfig).doesNotContain("timeout");
    }

    @Test
    void getConfigFileOverrideDesk() throws JsonProcessingException {
        Device device = new Device();
        device.setMacAddress("aa-bb-cc-dd-ee-ff");
        device.setOverrideFragment("domain=sip.anotherdomain.com\nport=5161\ntimeout=5");
        device.setPassword("doe");
        device.setUsername("john");
        device.setModel(Device.DeviceModel.DESK);

        ConfigurationFileOverride configFileOverride = mapper.getConfigFileOverride(device, "56F,95A");
        assertThat(configFileOverride).isNotNull();
        assertThat(configFileOverride.getTimeout()).isEqualTo(5);
    }

    @Test
    void getConfigFileOverrideConference() throws JsonProcessingException {
        Device device = new Device();
        device.setMacAddress("aa-bb-cc-dd-ee-ff");
        device.setOverrideFragment("{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",\"timeout\":9}");
        device.setPassword("doe");
        device.setUsername("john");
        device.setModel(Device.DeviceModel.CONFERENCE);

        ConfigurationFileOverride configFileOverride = mapper.getConfigFileOverride(device, "56F,95A");
        assertThat(configFileOverride).isNotNull();
        assertThat(configFileOverride.getTimeout()).isEqualTo(9);
    }

    @Test
    void convertToConfigurationOverrideJsonString() throws JsonProcessingException {
        String result = mapper.convertToConfigurationOverrideJsonString(new ConfigurationFileOverride());

        assertThat(result).contains("timeout");
    }
}
