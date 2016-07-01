package br.com.infox.epp.fluxo.view;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.HistoricoProcessDefinition;
import br.com.infox.epp.fluxo.list.HistoricoProcessDefinitionList;
import br.com.infox.epp.fluxo.service.HistoricoProcessDefinitionService;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Named
@ViewScoped
public class HistoricoProcessDefinitionView implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private HistoricoProcessDefinitionList historicoProcessDefinitionList;
	@Inject
	private HistoricoProcessDefinitionService historicoProcessDefinitionService;
	
	private HistoricoProcessDefinition historicoSelecionado;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	@ExceptionHandled(successMessage = "Definição restaurada com sucesso")
	public void restaurar(HistoricoProcessDefinition historicoProcessDefinition) {
		Fluxo fluxo = historicoProcessDefinitionService.restaurar(historicoProcessDefinition);
		historicoProcessDefinitionList.refresh();
		ProcessBuilder.instance().load(fluxo);
	}
	
	public void selecionarHistorico(HistoricoProcessDefinition historicoProcessDefinition) {
		this.historicoSelecionado = historicoProcessDefinition;
	}
	
	public HistoricoProcessDefinition getHistoricoSelecionado() {
		return historicoSelecionado;
	}
	
	public String getDataAlteracaoFormatada() {
		if (historicoSelecionado == null) {
			return null;
		}
		return dateFormat.format(historicoSelecionado.getDataAlteracao());
	}
}
