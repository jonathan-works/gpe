package br.com.infox.epp.usuario;

public interface ExternalAuthenticationService {
	boolean authenticate(String username, String password);
}
