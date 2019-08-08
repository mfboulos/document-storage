package com.boulos.documentstorage.service;

import org.h2.engine.Database;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.boulos.documentstorage.config.AppConfig;
import com.boulos.documentstorage.exception.DocumentNotFoundException;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentStorageServiceTest {
	@Mock
	Database database;
	@Mock
	AppConfig appConfig;
	
	DocumentStorageService testDSS;
	
	@Before
	public void setUp() {
		when(appConfig.getDirectory()).thenReturn(".");
	}
	
	@Test(expected=DocumentNotFoundException.class)
	public void testLoadThrowsExceptionIfGivenInvalidDocId() throws DocumentNotFoundException {
		throw new DocumentNotFoundException();
	}
	
	@Test
	public void testLoadReturnsDocumentIfGivenValidDocId() throws DocumentNotFoundException {
		
	}

	@Test
	public void testDocumentIsStoredFromMultipartForm() {
		
	}

	@Test(expected=DocumentNotFoundException.class)
	public void testUpdateThrowsExceptionIfGivenInvalidDocId() throws DocumentNotFoundException {
		throw new DocumentNotFoundException();
	}

	@Test
	public void testUpdateOverwritesDocumentWithValidDocId() throws DocumentNotFoundException {
		
	}
	
	@Test(expected=DocumentNotFoundException.class)
	public void testDeleteThrowsExceptionIfGivenInvalidDocId() throws DocumentNotFoundException {
		throw new DocumentNotFoundException();
	}
	
	@Test
	public void testDocumentIsDeletedIfGivenValidDocId() throws DocumentNotFoundException {
		
	}
}
