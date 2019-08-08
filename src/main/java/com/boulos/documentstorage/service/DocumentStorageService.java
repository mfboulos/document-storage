package com.boulos.documentstorage.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.boulos.documentstorage.config.AppConfig;
import com.boulos.documentstorage.database.DocumentRepository;
import com.boulos.documentstorage.exception.DocumentNotFoundException;
import com.boulos.documentstorage.model.Document;
import com.boulos.documentstorage.model.DocumentMetadata;

import lombok.Setter;

@Component
@Setter
public class DocumentStorageService implements StorageService {
	private DocumentRepository repo;
	private Path storageDirectory;
	
	public DocumentStorageService() {}
	
	@Autowired
	public DocumentStorageService(AppConfig config, DocumentRepository repo) {
		this.storageDirectory = Paths.get(config.getDirectory());
		this.repo = repo;
	}
	
	@Override
	public Document load(String docId) throws DocumentNotFoundException {
		// just in case people are trying to be funny here
		String safeDocId = FilenameUtils.getBaseName(docId);
		Optional<DocumentMetadata> optional = repo.findById(safeDocId);
		
		// if we don't find any metadata for our id, throw an exception
		if (!optional.isPresent()) {
			throw new DocumentNotFoundException();
		}
		
		Path file = storageDirectory.resolve(safeDocId + "." + optional.get().getExtension());
		Resource resource = loadFromPath(file);
		return new Document(optional.get(), resource);
	}

	@Override
	public String store(MultipartFile file) {
		String id = generateId();
		String name = FilenameUtils
				.getBaseName(file.getOriginalFilename());
		String extension = FilenameUtils
				.getExtension(file.getOriginalFilename())
				.toLowerCase();
		DocumentMetadata newMeta = new DocumentMetadata(id, name, 0L, extension);
		
		try (InputStream stream = file.getInputStream()) {
			long bytes = copy(stream, this.storageDirectory
					.resolve(newMeta.getStoredFileName()));
			newMeta.setBytes(bytes);
			repo.save(newMeta);
		} catch (IOException e) {
			// would suck if this happened
			e.printStackTrace();
		}
		
		return id;
	}

	@Override
	public void update(String docId, MultipartFile file) throws DocumentNotFoundException {
		// just in case people are trying to be funny here
		String safeDocId = FilenameUtils.getBaseName(docId);
		
		Optional<DocumentMetadata> optional = repo.findById(safeDocId);
		
		// if we don't find any metadata for our id, throw an exception
		if (!optional.isPresent()) {
			throw new DocumentNotFoundException();
		}
		
		DocumentMetadata meta = optional.get();
		
		try (InputStream stream = file.getInputStream()) {
			delete(this.storageDirectory.resolve(meta.getStoredFileName()));
			meta.setName(FilenameUtils.getBaseName(file.getOriginalFilename()));
			meta.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
			meta.setBytes(copy(stream, this.storageDirectory.resolve(meta.getStoredFileName())));
			repo.save(meta);
		} catch (IOException e) {
			// would suck if this happened
			e.printStackTrace();
		}
	}

	@Override
	public void delete(String docId) throws DocumentNotFoundException {
		// just in case people are trying to be funny here
		String safeDocId = FilenameUtils.getBaseName(docId);
		
		Optional<DocumentMetadata> optional = repo.findById(safeDocId);
		
		// if we don't find any metadata for our id, throw an exception
		if (!optional.isPresent()) {
			throw new DocumentNotFoundException();
		}
		
		DocumentMetadata meta = optional.get();
		
		try {
			delete(this.storageDirectory.resolve(meta.getStoredFileName()));
			repo.deleteById(safeDocId);
		} catch (IOException e) {
			// you know the drill, this would be bad but don't really expect it
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates a unique alphanumeric id with 20 characters.
	 * 
	 * @return Randomly generated alphanumeric String of length 20
	 */
	public String generateId() {
		String id = RandomStringUtils.randomAlphanumeric(20).toLowerCase();
		
		while (repo.existsById(id)) {
			id = RandomStringUtils.randomAlphanumeric(20).toLowerCase();
		}
		
		return id;
	}
	
	/**
	 * Loads a {@link Resource} from a {@link Path}.
	 * 
	 * @param path
	 * @return Resource loaded, or null if URI is malformed
	 * @throws DocumentNotFoundException
	 */
	public Resource loadFromPath(Path path) throws DocumentNotFoundException {
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
			
			// if for whatever reason our db doesn't match our file storage,
			// any indication that our resource isn't accessible also throws
			// the same exception
			if (!resource.exists() || !resource.isReadable()) {
				throw new DocumentNotFoundException();
			}
		} catch (MalformedURLException e) {
			// would suck if this happened
			e.printStackTrace();
		}
		
		return resource;
	}
	
	/**
	 * File wrapper for {@link Files#copy(InputStream, Path, CopyOption...)}
	 * for unit testability.
	 * 
	 * @param in
	 * @param target
	 * @param options
	 * @return
	 * @throws IOException
	 */
	public long copy(InputStream in, Path target, CopyOption... options) throws IOException {
		return Files.copy(in, target, options);
	}
	
	/**
	 * File wrapper for {@link Files#delete(Path)} for unit testability.
	 * 
	 * @param target
	 * @throws IOException
	 */
	public void delete(Path target) throws IOException {
		Files.delete(target);
	}
}
