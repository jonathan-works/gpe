package br.com.infox.core.report;

import java.io.Serializable;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;

@Singleton
@Startup
@Named
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AutomaticReportController implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String KEY_HEADER_NAME = "X-Key";
	
	private UUID key;
	
	@PostConstruct
	private void init() {
		key = UUID.randomUUID();
	}
	
	public UUID getKey() {
		return key;
	}
	
	public boolean isValid(String candidateKey) {
		try {
			UUID candidate = UUID.fromString(candidateKey);
			return key.equals(candidate);
		} catch (NullPointerException | IllegalArgumentException e) {
			return false;
		}
	}
}
