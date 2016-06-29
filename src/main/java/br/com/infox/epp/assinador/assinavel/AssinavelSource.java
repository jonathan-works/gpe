package br.com.infox.epp.assinador.assinavel;

public interface AssinavelSource {

	public byte[] digest(TipoHash tipoHash);
	
}
