package com.boulos.documentstorage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.boulos.documentstorage.exception.DocumentNotFoundException;
import com.boulos.documentstorage.model.Document;

public interface StorageService {
	/**
	 * Loads file as a {@link Resource} from the given {@code docId} and
	 * returns it.
	 * 
	 * @param docId ID of the file
	 * @return file as a {@link Document}
	 */
	Document load(String docId) throws DocumentNotFoundException;
	
	/**
	 * Stores a file according to a {@link MultipartFile} payload.
	 * 
	 * @param file Payload containing file contents
	 * @return Randomly generated ID as a String
	 */
	String store(MultipartFile file);
	
	/**
	 * Updates the file referenced by {@code docId} according to a
	 * {@link MultipartFile} payload.
	 * 
	 * @param docId ID of the file
	 * @param file Payload containing contents to overwrite with
	 */
	void update(String docId, MultipartFile file) throws DocumentNotFoundException;
	
	/**
	 * Deletes the file referenced by {@code docId}.
	 * 
	 * @param docId ID of the file
	 */
	void delete(String docId) throws DocumentNotFoundException;
}
