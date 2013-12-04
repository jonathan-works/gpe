package br.com.infox.epp.processo.variavel.action;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.itx.util.EntityUtil;

@Name(VariavelProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class VariavelProcessoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "variavelProcessoAction";
	
	private Boolean possuiVariaveis;
	private List<VariavelProcesso> variaveis;
	private ProcessoEpa processoEpa;
	
	@In
	private VariavelProcessoService variavelProcessoService;
	
	public Boolean possuiVariaveis() {
		if (this.possuiVariaveis == null) {
			this.possuiVariaveis = !getVariaveis().isEmpty();
		}
		return this.possuiVariaveis;
	}
	
	public List<VariavelProcesso> getVariaveis() {
		if (this.variaveis == null) {
			this.variaveis = variavelProcessoService.getVariaveis(processoEpa);
		}
		return this.variaveis;
	}

	public void setProcessoEpa(ProcessoEpa processoEpa) {
		this.processoEpa = processoEpa;
	}
	
	public void setProcesso(Processo processo) {
		this.processoEpa = EntityUtil.find(ProcessoEpa.class, processo.getIdProcesso());
	}
	
	public void save() {
		for (VariavelProcesso variavel : getVariaveis()) {
			variavelProcessoService.save(variavel);
		}
		FacesMessages.instance().add("#{messages['VariavelProcesso_updated']}");
	}
}
