package br.com.infox.core.certificado.crl.jobs;

public interface CrlCertTestJobActionListner {
	
	public void execute(boolean revoked);
	
}