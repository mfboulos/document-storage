package com.boulos.documentstorage.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DocumentMetadata {
	@Id
	@GenericGenerator(
			name="doc-id-generator",
			strategy="com.boulos.documentstorage.database.DocumentIdGenerator")
	private String id;
	private Long bytes;
	private String extension;
	
	public DocumentMetadata(Long bytes, String extension) {
		this.bytes = bytes;
		this.extension = extension;
	}
}
