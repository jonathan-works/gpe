package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.manager.ParteProcessoManager;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Processo;
import br.com.itx.component.AbstractHome;

@Name(ProcessoEpaHome.NAME)
@Scope(ScopeType.PAGE)
public class ProcessoEpaHome extends AbstractHome<ProcessoEpa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoEpaHome";
	
	@In private ParteProcessoManager parteProcessoManager;
	
	public void incluirParteProcesso(Processo processo, String tipoPessoa){
		parteProcessoManager.incluir(processo, tipoPessoa);
	}
	
	public void inativarParteProcesso(ParteProcesso parteProcesso){
		parteProcessoManager.inativar(parteProcesso);
	}
	
	public void carregaPessoa(String tipoPessoa, String codigo){
		parteProcessoManager.carregaPessoa(tipoPessoa, codigo);
	}

}
