package com.boulos.documentstorage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

/**
 * Document storage configurations.
 * 
 * @author Boulos
 *
 */
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix="storage")
@Getter
@Setter
public class StorageConfig {
	private String directory;
}
