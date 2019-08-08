package com.boulos.documentstorage.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persistent metadata for saved documents.
 * 
 * @author Boulos
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadata {
	@Id
	private String id;
	private String name;
	private Long bytes;
	private String extension;
	
	public DocumentMetadata(String id, MultipartFile file) {
		this(id, FilenameUtils.getBaseName(file.getOriginalFilename()),
				file.getSize(),
				FilenameUtils.getExtension(file.getOriginalFilename()));
	}
	
	public String getStoredFileName() {
		return this.id + "." + this.extension;
	}
	
	public String getOriginalFileName() {
		return this.name + "." + this.extension;
	}
}
