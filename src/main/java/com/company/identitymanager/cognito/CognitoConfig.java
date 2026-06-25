package com.company.identitymanager.cognito;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.cognito")
public class CognitoConfig {

    private String userPoolId;

    private String clientId;

    private String region;
}