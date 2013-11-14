package br.com.infox.certificado.crl.jobs;

public interface CrlCertTestJobActionListner {
	
	void execute(boolean revoked);
	
}