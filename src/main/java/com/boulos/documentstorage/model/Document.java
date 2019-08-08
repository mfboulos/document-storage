package com.boulos.documentstorage.model;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Document containing {@link DocumentMetadata} and {@link Resource} information.
 * 
 * @author Boulos
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class Document {
	DocumentMetadata meta;
	Resource resource;
}
