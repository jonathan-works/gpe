package br.com.infox.core.certificado.crl.jobs;

public interface CrlCertTestJobActionListner {
	
	void execute(boolean revoked);
	
}