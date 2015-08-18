package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class VisualizarComunicacaoAction implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private DocumentoComunicacaoList documentoComunicacaoList = ComponentUtil.getComponent(DocumentoComunicacaoList.NAME);
	
	private Processo processoComunicacao;
	private DestinatarioModeloComunicacao destinatario;
	
	@PostConstruct
	public void init(){
		this.processoComunicacao = JbpmUtil.getProcesso();
		MetadadoProcesso metadadoDestinatario = processoComunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
		if (metadadoDestinatario != null) {
			destinatario = metadadoDestinatario.getValue();
			documentoComunicacaoList.setModeloComunicacao(destinatario.getModeloComunicacao());
		}
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
	}
	
	public Date getDataEnvio(){
		return processoComunicacao.getDataInicio();
	}
}
