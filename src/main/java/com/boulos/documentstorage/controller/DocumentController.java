package com.boulos.documentstorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.boulos.documentstorage.exception.DocumentNotFoundException;
import com.boulos.documentstorage.service.StorageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/storage/documents")
@AllArgsConstructor(onConstructor_= {@Autowired})
public class DocumentController {
	private final StorageService storageService;
	
	@GetMapping("/{docId}")
	public ResponseEntity<Resource> get(@PathVariable String docId) throws DocumentNotFoundException {
		Resource file = storageService.load(docId);
		return new ResponseEntity<>(file, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<String> create(@RequestParam MultipartFile file) {
		String docId = storageService.store(file);
		return new ResponseEntity<>(docId, HttpStatus.CREATED);
	}
	
	@PutMapping("/{docId}")
	public ResponseEntity<?> update(@PathVariable String docId, @RequestParam MultipartFile file) throws DocumentNotFoundException {
		storageService.update(docId, file);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/{docId}")
	public ResponseEntity<?> delete(@PathVariable String docId) throws DocumentNotFoundException {
		storageService.delete(docId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
