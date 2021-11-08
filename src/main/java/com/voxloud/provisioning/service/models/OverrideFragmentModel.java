package com.voxloud.provisioning.service.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OverrideFragmentModel {
    private String domain;
    private String port;
    private int timeout;
}
