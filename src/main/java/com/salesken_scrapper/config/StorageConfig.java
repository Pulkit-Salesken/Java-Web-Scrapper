package com.salesken_scrapper.config;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ai.salesken.common.service.storage.FileStorageFactory;
import ai.salesken.common.service.storage.StorageProvider;
import ai.salesken.common.service.storage.credentials.StorageCredentials;

@Configuration
public class StorageConfig {
	@Value("${storage.gcp.credential_file:}")
	String gcpCredentialPath;
	@Value("${storage.provider}")
	String storageProvider;

	@Value("${storage.aws.access_key:}")
	String awsAccessKey;
	@Value("${storage.aws.secret_key:}")
	String awsSecretKey;

	@PostConstruct
	public void init() {
		StorageCredentials storageCredentials = new StorageCredentials(gcpCredentialPath, awsAccessKey, awsSecretKey);
		FileStorageFactory.getStorageService(StorageProvider.valueOf(storageProvider), storageCredentials);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
}
