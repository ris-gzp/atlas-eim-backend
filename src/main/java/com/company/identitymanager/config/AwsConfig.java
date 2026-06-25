package com.company.identitymanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.ses.SesClient;

import software.amazon.awssdk.services.cognitoidentityprovider
        .CognitoIdentityProviderClient;

@Configuration
public class AwsConfig {
	
	@Bean
	public CognitoIdentityProviderClient cognitoClient() {

	    return CognitoIdentityProviderClient.builder()
	            .region(Region.AP_SOUTH_1)
	            .build();
	}
	
	@Bean
	public SesClient sesClient() {

	    return SesClient.builder()
	            .region(Region.AP_SOUTH_1)
	            .build();
	}
}