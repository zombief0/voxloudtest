package com.voxloud.provisioning.controller;

import com.voxloud.provisioning.service.ProvisioningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProvisioningController.class)
class ProvisioningControllerTestIT {
    @MockBean
    private ProvisioningService provisioningService;

    @Autowired
    private MockMvc mockMvc;

    private String jsonConfig;
    private String plainTextConfig;
    @BeforeEach
    void setUp() {
        jsonConfig = "{\n" +
                "  \"username\" : \"john\"," +
                "  \"password\" : \"doe\"," +
                "  \"domain\" : \"sip.anotherdomain.com\"," +
                "  \"port\" : \"5161\"," +
                "  \"codecs\" : [\"G711\",\"G729\",\"OPUS\"]," +
                "  \"timeout\" : 10" +
                "}";
        plainTextConfig = "username=john\n" +
                "password=doe\n" +
                "domain=sip.anotherdomain.com\n" +
                "port=5161\n" +
                "codecs=G711,G729,OPUS\n" +
                "timeout=10\n";
    }

    @Test
    void getConfigMacNotExist() throws Exception {
        given(provisioningService.getProvisioningFile(anyString())).willReturn(null);

        mockMvc.perform(get("/api/v1/provisioning/b2-c3-d4-e5-f6"))
                .andExpect(status().isNotFound());

        then(provisioningService).should().getProvisioningFile(anyString());
    }

    @Test
    void getConfigMacConferenceDevice() throws Exception {

        given(provisioningService.getProvisioningFile(anyString())).willReturn(jsonConfig);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/provisioning/b2-c3-d4-e5-f6"))
                .andExpect(status().isOk()).andReturn();
        String actualJson = mvcResult.getResponse().getContentAsString();

        then(provisioningService).should().getProvisioningFile(anyString());
        assertThat(actualJson).isEqualTo(jsonConfig);
    }

    @Test
    void getConfigMacDeskDevice() throws Exception {

        given(provisioningService.getProvisioningFile(anyString())).willReturn(plainTextConfig);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/provisioning/b2-c3-d4-e5-f6"))
                .andExpect(status().isOk()).andReturn();
        String actualTextConfig = mvcResult.getResponse().getContentAsString();

        then(provisioningService).should().getProvisioningFile(anyString());
        assertThat(actualTextConfig).isEqualTo(plainTextConfig);
    }

}
