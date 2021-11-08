package com.voxloud.provisioning.service.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConfigurationFileOverride {
    private String username;
    private String password;
    private String domain;
    private String port;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> codecs;
    private int timeout;
}
