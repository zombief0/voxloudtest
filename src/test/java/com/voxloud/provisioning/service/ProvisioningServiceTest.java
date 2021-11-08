package com.voxloud.provisioning.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.service.mapper.Mapper;
import com.voxloud.provisioning.service.models.ConfigurationFile;
import com.voxloud.provisioning.service.models.ConfigurationFileOverride;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProvisioningServiceTest {
    @InjectMocks
    private ProvisioningServiceImpl provisioningService;

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private Environment environment;
    @Mock
    private Mapper mapper;

    private Device device;
    private ConfigurationFile configurationFile;
    private ConfigurationFileOverride configurationFileOverride;


    @BeforeEach
    void setUp() {
        device = new Device();
        device.setMacAddress("aa-bb-cc-dd-ee-ff");
        device.setOverrideFragment(null);
        device.setPassword("doe");
        device.setUsername("john");
        configurationFile = new ConfigurationFile();
        configurationFile.setDomain("sip.voxloud.com");
        configurationFile.setCodecs(Arrays.asList("G711","G729","OPUS"));
        configurationFile.setPassword(device.getPassword());
        configurationFile.setUsername(device.getUsername());
        configurationFile.setPort("5060");
        configurationFileOverride = new ConfigurationFileOverride();
        configurationFileOverride.setTimeout(10);
        configurationFileOverride.setDomain("sip.voxloud.com");
        configurationFileOverride.setCodecs(Arrays.asList("G711","G729","OPUS"));
        configurationFileOverride.setPassword(device.getPassword());
        configurationFileOverride.setUsername(device.getUsername());
        configurationFileOverride.setPort("5060");
    }

    @Test
    void getProvisioningFileDeviceNotFound() throws JsonProcessingException {
        given(deviceRepository.findById(anyString())).willReturn(Optional.empty());

        String provisioningFile = provisioningService.getProvisioningFile("aa-bb-cc-dd-f1");

        then(deviceRepository).should().findById(anyString());
        then(environment).shouldHaveNoInteractions();
        then(mapper).shouldHaveNoInteractions();
        assertThat(provisioningFile).isNull();
    }

    @Test
    void getProvisioningFileDeviceDeskNoOverride() throws JsonProcessingException {
        String baseKeyword = "provisioning.";
        given(environment.getProperty(baseKeyword + "domain")).willReturn("sip.voxloud.com");
        given(environment.getProperty(baseKeyword + "port")).willReturn("5060");
        given(environment.getProperty(baseKeyword + "codecs")).willReturn("G711,G729,OPUS");
        device.setModel(Device.DeviceModel.DESK);
        String plainTextConfig = "username=john\n" +
                "password=doe\n" +
                "domain=sip.voxloud.com\n" +
                "port=5060\n" +
                "codecs=G711,G729,OPUS";

        given(deviceRepository.findById(anyString())).willReturn(Optional.of(device));
        given(mapper.getConfigFileModel(any(Device.class), anyString(), anyString(), anyString())).willReturn(configurationFile);

        String provisioningFile = provisioningService.getProvisioningFile("aa-bb-cc-dd-f1");

        then(deviceRepository).should().findById(anyString());
        then(environment).should(times(3)).getProperty(anyString());
        then(mapper).should().getConfigFileModel(any(Device.class), anyString(), anyString(), anyString());
        then(mapper).shouldHaveNoMoreInteractions();
        assertThat(provisioningFile).isEqualTo(plainTextConfig);
    }

    @Test
    void getProvisioningFileDeviceConferenceNoOverride() throws JsonProcessingException {
        String baseKeyword = "provisioning.";
        given(environment.getProperty(baseKeyword + "domain")).willReturn("sip.voxloud.com");
        given(environment.getProperty(baseKeyword + "port")).willReturn("5060");
        given(environment.getProperty(baseKeyword + "codecs")).willReturn("G711,G729,OPUS");

        device.setModel(Device.DeviceModel.CONFERENCE);

        String jsonConfigFile = "{\n" +
                "  \"username\" : \"john\"," +
                "  \"password\" : \"doe\"," +
                "  \"domain\" : \"sip.anotherdomain.com\"," +
                "  \"port\" : \"5161\"," +
                "  \"codecs\" : [\"G711\",\"G729\",\"OPUS\"]," +
                "}";

        given(deviceRepository.findById(anyString())).willReturn(Optional.of(device));
        given(mapper.getConfigFileModel(any(Device.class), anyString(), anyString(), anyString())).willReturn(configurationFile);
        given(mapper.convertToConfigurationJsonString(any(ConfigurationFile.class))).willReturn(jsonConfigFile);

        String provisioningFile = provisioningService.getProvisioningFile("f1-e2-d3-c4-b5-a6");

        then(deviceRepository).should().findById(anyString());
        then(environment).should(times(3)).getProperty(anyString());
        then(mapper).should().getConfigFileModel(any(Device.class), anyString(), anyString(), anyString());
        then(mapper).should().convertToConfigurationJsonString(any(ConfigurationFile.class));
        assertThat(provisioningFile).isEqualTo(jsonConfigFile);
    }

    @Test
    void getProvisioningFileDeviceConferenceOverride() throws JsonProcessingException {
        String baseKeyword = "provisioning.";
        given(environment.getProperty(baseKeyword + "codecs")).willReturn("G711,G729,OPUS");

        device.setModel(Device.DeviceModel.CONFERENCE);
        device.setOverrideFragment("{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",\"timeout\":10}");

        String jsonConfigFile = "{\n" +
                "  \"username\" : \"john\"," +
                "  \"password\" : \"doe\"," +
                "  \"domain\" : \"sip.anotherdomain.com\"," +
                "  \"port\" : \"5161\"," +
                "  \"codecs\" : [\"G711\",\"G729\",\"OPUS\"]," +
                "  \"timeout\" : 10" +
                "}";

        given(deviceRepository.findById(anyString())).willReturn(Optional.of(device));
        given(mapper.getConfigFileOverride(any(Device.class), anyString())).willReturn(configurationFileOverride);
        given(mapper.convertToConfigurationOverrideJsonString(any(ConfigurationFileOverride.class))).willReturn(jsonConfigFile);

        String provisioningFile = provisioningService.getProvisioningFile("1a-2b-3c-4d-5e-6f");

        then(deviceRepository).should().findById(anyString());
        then(environment).should().getProperty(anyString());
        then(mapper).should().getConfigFileOverride(any(Device.class), anyString());
        then(mapper).should().convertToConfigurationOverrideJsonString(any(ConfigurationFileOverride.class));
        assertThat(provisioningFile).isEqualTo(jsonConfigFile);
    }

    @Test
    void getProvisioningFileDeviceDeskOverride() throws JsonProcessingException {
        String baseKeyword = "provisioning.";
        given(environment.getProperty(baseKeyword + "codecs")).willReturn("G711,G729,OPUS");
        device.setModel(Device.DeviceModel.DESK);
        device.setOverrideFragment("domain=sip.anotherdomain.com\nport=5161\ntimeout=10");
        String plainTextConfig = "username=john\n" +
                "password=doe\n" +
                "domain=sip.voxloud.com\n" +
                "port=5060\n" +
                "codecs=G711,G729,OPUS\n" +
                "timeout=10";

        given(deviceRepository.findById(anyString())).willReturn(Optional.of(device));
        given(mapper.getConfigFileOverride(any(Device.class), anyString())).willReturn(configurationFileOverride);

        String provisioningFile = provisioningService.getProvisioningFile("a1-b2-c3-d4-e5-f6");

        then(deviceRepository).should().findById(anyString());
        then(environment).should().getProperty(anyString());
        then(mapper).should().getConfigFileOverride(any(Device.class), anyString());
        then(mapper).shouldHaveNoMoreInteractions();
        assertThat(provisioningFile).isEqualTo(plainTextConfig);
    }
}
