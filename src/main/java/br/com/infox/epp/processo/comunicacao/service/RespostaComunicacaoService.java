package br.com.infox.epp.processo.comunicacao.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.processo.comunicacao.dao.DocumentoRespostaComunicacaoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;

@Name(RespostaComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class RespostaComunicacaoService {
	public static final String NAME = "respostaComunicacaoService";
	
	@In
	private DocumentoRespostaComunicacaoDAO documentoRespostaComunicacaoDAO;
	
	public void enviarResposta(Documento resposta) {
		
	}
}
