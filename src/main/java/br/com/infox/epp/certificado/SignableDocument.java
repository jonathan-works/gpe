package br.com.infox.epp.certificado;

import java.util.UUID;

public interface SignableDocument {

	public String getMD5();

	public UUID getUuid();
}
