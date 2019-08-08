package com.boulos.documentstorage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix="storage")
@Getter
@Setter
public class AppConfig {
	private String directory;
}
