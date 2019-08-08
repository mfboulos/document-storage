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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.boulos.documentstorage.config.StorageConfig;
import com.boulos.documentstorage.database.DocumentRepository;
import com.boulos.documentstorage.exception.DocumentNotFoundException;
import com.boulos.documentstorage.model.Document;
import com.boulos.documentstorage.model.DocumentMetadata;

import lombok.Setter;

/**
 * Implementation of {@link StorageService} which autowires a configured storage
 * directory and metadata repository for document storage.
 * 
 * @author Boulos
 *
 */
@Component
@Setter
public class DocumentStorageService implements StorageService {
	private DocumentRepository repo;
	private Path storageDirectory;
	
	// just for unit testing :)
	DocumentStorageService() {}
	
	@Autowired
	public DocumentStorageService(StorageConfig config, DocumentRepository repo) {
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
		
		DocumentMetadata meta = optional.get();
		
		// get our file as stored, and return it with its metadata
		Path file = storageDirectory.resolve(meta.getStoredFileName());
		Resource resource = loadFromPath(file);
		return new Document(meta, resource);
	}

	@Override
	public String store(MultipartFile file) {
		String id = generateId();
		String name = FilenameUtils
				.getBaseName(file.getOriginalFilename());
		String extension = FilenameUtils
				.getExtension(file.getOriginalFilename())
				.toLowerCase();
		
		// create metadata for our new file, we'll set bytes later
		DocumentMetadata newMeta = new DocumentMetadata(id, name, 0L, extension);
		
		try (InputStream stream = file.getInputStream()) {
			// save the file as {id}.{extension}, so we're guaranteed
			// uniqueness among saved files
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
			// since the extension can be different, the filename might be too
			// so we're deleting and recreating the file instead of overwriting it
			delete(this.storageDirectory.resolve(meta.getStoredFileName()));
			
			// also need to update metadata
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
		String id = RandomStringUtils.randomAlphanumeric(20);
		
		while (repo.existsById(id)) {
			id = RandomStringUtils.randomAlphanumeric(20);
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
