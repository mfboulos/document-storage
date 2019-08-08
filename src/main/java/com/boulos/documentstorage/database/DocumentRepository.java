package com.boulos.documentstorage.database;

import org.springframework.data.repository.CrudRepository;

import com.boulos.documentstorage.model.DocumentMetadata;;

public interface DocumentRepository extends CrudRepository<DocumentMetadata, Long>{

}
