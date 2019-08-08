package com.boulos.documentstorage.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.boulos.documentstorage.exception.DocumentNotFoundException;
import com.boulos.documentstorage.model.Document;
import com.boulos.documentstorage.service.StorageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/storage/documents")
@AllArgsConstructor(onConstructor_= {@Autowired})
public class DocumentController {
	private final StorageService storageService;
	
	/**
	 * Handles {@code HTTP GET} by invoking the {@link StorageService} to load
	 * the resource identified by {@code docId}.
	 * 
	 * @param docId Id represented in the URL path
	 * @param req Servlet request to extract context for MIME type
	 * @return Resource as a file attachment
	 * @throws DocumentNotFoundException if {@code docId} is invalid
	 */
	@GetMapping("/{docId}")
	public ResponseEntity<Resource> get(@PathVariable String docId, HttpServletRequest req) throws DocumentNotFoundException {
		Document file = storageService.load(docId);
		MediaType contentType;
		try {
			contentType = MediaType.parseMediaType(
					req.getServletContext().getMimeType(
							file.getResource().getFile().getAbsolutePath()));
		} catch (IOException e) {
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		}
		
		return ResponseEntity.ok()
				.contentType(contentType)
				.header(HttpHeaders.CONTENT_DISPOSITION, String.format(
						"attachment; filename=\"%s\"", file.getMeta().getOriginalFileName()))
				.body(file.getResource());
	}
	
	/**
	 * Handles {@code HTTP POST} by invoking the {@link StorageService} to
	 * store the file in the request payload.
	 * 
	 * @param file File sent through the HTTP request
	 * @return docId of the saved document
	 */
	@PostMapping
	public ResponseEntity<String> create(@RequestParam MultipartFile file) {
		String docId = storageService.store(file);
		return new ResponseEntity<>(docId, HttpStatus.CREATED);
	}
	
	/**
	 * Handles {@code HTTP PUT} by invoking the {@link StorageService} to
	 * update the file in the request payload identified by {@link docId}.
	 * 
	 * @param docId Id of the file to update
	 * @param file File to update to
	 * @return Response with no content
	 * @throws DocumentNotFoundException if {@code docId} is invalid
	 */
	@PutMapping("/{docId}")
	public ResponseEntity<?> update(
			@PathVariable String docId,
			@RequestParam MultipartFile file) throws DocumentNotFoundException {
		storageService.update(docId, file);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Handles {@code HTTP DELETE} by invoking the {@link StorageService} to
	 * delete the file identified by {@link docId}.
	 * 
	 * @param docId Id of the file to delete
	 * @return Response with no content
	 * @throws DocumentNotFoundException if {@code docId} is invalid
	 */
	@DeleteMapping("/{docId}")
	public ResponseEntity<?> delete(
			@PathVariable String docId) throws DocumentNotFoundException {
		storageService.delete(docId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
