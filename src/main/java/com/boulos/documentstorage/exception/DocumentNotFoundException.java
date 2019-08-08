package com.boulos.documentstorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Signals that an invalid {@code docId} was given, or that the document
 * reference by such {@code docId} was unable to be accessed.
 * 
 * @author Boulos
 *
 */
@ResponseStatus(code=HttpStatus.NOT_FOUND, reason="No document found with that ID")
public class DocumentNotFoundException extends Exception {
	private static final long serialVersionUID = -8211113214880104283L;
}
