package br.com.infox.epp.assinador.view;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.assinador.AssinadorGroupService;
import br.com.infox.epp.assinador.AssinadorGroupService.StatusToken;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@Named
@ViewScoped
public class AssinadorController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AssinadorService assinadorService;
	@Inject
	private AssinadorGroupService groupService;
	
	private String token;
	
	public String criarListaDocumentos(List<DocumentoBin> listaDocumentos) {
		this.token = assinadorService.criarListaDocumentos(listaDocumentos);
		return this.token;
	}
	
	public boolean isFinalizado() {
		StatusToken status = groupService.getStatus(token);
		return status != StatusToken.AGUARDANDO_ASSINATURA;
	}
	
	public boolean isSucesso() {
		StatusToken status = groupService.getStatus(token);
		return status == StatusToken.SUCESSO;
	}
	
}
