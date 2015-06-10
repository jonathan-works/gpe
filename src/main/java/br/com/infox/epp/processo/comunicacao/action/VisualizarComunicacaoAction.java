package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class VisualizarComunicacaoAction implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Processo processoComunicacao;
	private DestinatarioModeloComunicacao destinatario;
	private List<Documento> documentosComunicacao;
	
	@PostConstruct
	public void init(){
		this.processoComunicacao = JbpmUtil.getProcesso();
		this.destinatario = processoComunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
	}
	
	public List<Documento> getDocumentosComunicacao(){
		if(documentosComunicacao == null){
			documentosComunicacao = ComponentUtil.<ModeloComunicacaoManager>getComponent(ModeloComunicacaoManager.NAME).getDocumentosByModeloComunicacao(destinatario.getModeloComunicacao());
		}
		return documentosComunicacao;
	}
	
	public Date getDataEnvio(){
		return processoComunicacao.getDataInicio();
	}
}
