package com.boulos.documentstorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class DocumentStorageWebApp extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		SpringApplication.run(DocumentStorageWebApp.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DocumentStorageWebApp.class);
    }
}
