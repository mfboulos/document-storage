package com.boulos.documentstorage.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.boulos.documentstorage.config.AppConfig;
import com.boulos.documentstorage.database.DocumentRepository;
import com.boulos.documentstorage.exception.DocumentNotFoundException;
import com.boulos.documentstorage.model.DocumentMetadata;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentStorageServiceTest {
	@Mock
	private DocumentRepository testRepo;
	@Mock
	private AppConfig appConfig;
	@Mock
	private MultipartFile testFile;
	@Mock
	private InputStream mockStream;
	@Spy
	private DocumentStorageService testDSS;
	
	private final Path testPath = Paths.get(".");
	
	@Before
	public void setUp() throws DocumentNotFoundException, IOException {
		when(testRepo.findById(any())).thenReturn(Optional.empty());
		when(testRepo.findById("25isfunnierthan24lol")).thenReturn(
				Optional.of(new DocumentMetadata("25isfunnierthan24lol", "gnome", 1000L, "jpg")));
		when(testFile.getOriginalFilename()).thenReturn("cool_dog.jpg");
		
		when(mockStream.read(any())).thenReturn(0);
		when(testFile.getInputStream()).thenReturn(mockStream);
		
		testDSS.setRepo(testRepo);
		testDSS.setStorageDirectory(testPath);
		
		doReturn(0L).when(testDSS).copy(eq(mockStream), any(), any());
		doNothing().when(testDSS).delete(any(Path.class));
	}
	
	@Test(expected=DocumentNotFoundException.class)
	public void testLoadThrowsExceptionIfGivenInvalidDocId() throws DocumentNotFoundException {
		testDSS.load("testid12320character");
	}
	
	@Test
	public void testLoadReturnsResourceIfGivenValidDocId() throws DocumentNotFoundException {
		Resource testResource = mock(Resource.class);
		when(testResource.exists()).thenReturn(true);
		when(testResource.isReadable()).thenReturn(true);
		when(testResource.getDescription()).thenReturn("Cool stuff");
		doReturn(testResource).when(testDSS)
				.loadFromPath(testPath.resolve("25isfunnierthan24lol.jpg"));
		Resource result = testDSS.load("25isfunnierthan24lol").getResource();
		
		assertEquals(testResource.getDescription(), result.getDescription());
	}

	@Test
	public void testDocumentIsStoredFromMultipartForm() throws IOException {
		testDSS.store(testFile);
		verify(testRepo).save(any());
		verify(testDSS).copy(eq(mockStream), any(), any());
	}

	@Test(expected=DocumentNotFoundException.class)
	public void testUpdateThrowsExceptionIfGivenInvalidDocId() throws DocumentNotFoundException {
		testDSS.update("testid12320character", testFile);
	}

	@Test
	public void testUpdateOverwritesDocumentWithValidDocId() throws IOException, DocumentNotFoundException {
		testDSS.update("25isfunnierthan24lol", testFile);
		verify(testRepo).save(any());
		verify(testDSS).copy(eq(mockStream), any(), any());
	}
	
	@Test(expected=DocumentNotFoundException.class)
	public void testDeleteThrowsExceptionIfGivenInvalidDocId() throws DocumentNotFoundException {
		testDSS.delete("testid12320character");
	}
	
	@Test
	public void testDocumentIsDeletedIfGivenValidDocId() throws DocumentNotFoundException {
		testDSS.delete("25isfunnierthan24lol");
		verify(testRepo).deleteById("25isfunnierthan24lol");
		verify(testDSS).delete("25isfunnierthan24lol");
	}
	
	@Test
	public void testGeneratedIdsAreValid() {
		String id = testDSS.generateId();
		assertEquals(20, id.length());
		assertTrue(StringUtils.isAlphanumeric(id));
	}
}
