package com.boulos.documentstorage.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DocumentMetadata {
	@Id
	private String id;
	private Long bytes;
	private String extension;
	
	public DocumentMetadata(String id, Long bytes, String extension) {
		this.id = id;
		this.bytes = bytes;
		this.extension = extension;
	}
	
	public DocumentMetadata(String id, MultipartFile file) {
		this(id, file.getSize(),
				FilenameUtils.getExtension(file.getOriginalFilename()));
	}
}
