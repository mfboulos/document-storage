package com.boulos.documentstorage.database;

import java.io.Serializable;
import java.util.stream.Stream;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class DocumentIdGenerator implements IdentifierGenerator {
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
		String query = String.format("select %s from %s", 
				session.getEntityPersister(obj.getClass().getName(), obj)
				.getIdentifierPropertyName(),
				obj.getClass().getSimpleName());

		Stream<String> ids = session.createQuery(query).stream();
		StringBuilder generatedId = new StringBuilder(RandomStringUtils.randomAlphanumeric(20));
		
		while(ids.anyMatch(id -> id.equalsIgnoreCase(generatedId.toString()))) {
			generatedId.delete(0, 20);
			generatedId.append(RandomStringUtils.randomAlphanumeric(20));
		}
		
		return generatedId;
	}
}
