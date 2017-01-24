package br.com.infox.epp.documento.publicacao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Documento;

@Named
@ViewScoped
public class PublicacaoDialogView implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<PublicacaoDocumento> publicacoes;

	public void setDocumento(Documento documento) {
	 // TODO corrigir para integrar epp 2.14
//		publicacoes = documento.getPublicacoes();
	}
	
	public List<PublicacaoDocumento> getPublicacoes() {
		return publicacoes;
	}
	
	
	
	

}
