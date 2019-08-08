package com.boulos.documentstorage.database;

import org.springframework.data.repository.CrudRepository;

import com.boulos.documentstorage.model.DocumentMetadata;;

/**
 * Interface for generic CRUD operations on {@link DocumentMetadata}.
 * 
 * @author Boulos
 *
 */
public interface DocumentRepository extends CrudRepository<DocumentMetadata, String>{

}
