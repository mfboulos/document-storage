package com.boulos.documentstorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND, reason="No document found with that ID")
public class DocumentNotFoundException extends Exception {
	private static final long serialVersionUID = -8211113214880104283L;
}
