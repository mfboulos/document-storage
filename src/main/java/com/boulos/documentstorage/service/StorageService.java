package com.boulos.documentstorage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	Resource load(String fileName);
	String store(MultipartFile file);
	void update(MultipartFile file);
	void delete(String fileName);
}
