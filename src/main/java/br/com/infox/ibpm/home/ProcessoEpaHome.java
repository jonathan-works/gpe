package br.com.infox.ibpm.home;

import javax.persistence.PersistenceException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.manager.ParteProcessoManager;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.manager.PessoaManager;
import br.com.itx.component.AbstractHome;

@Name(ProcessoEpaHome.NAME)
@Scope(ScopeType.PAGE)
public class ProcessoEpaHome extends AbstractHome<ProcessoEpa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoEpaHome";
	
	@In private PessoaManager pessoaManager;
	@In private ParteProcessoManager parteProcessoManager;
	
	public void incluirParteProcesso(Processo processo, String tipoPessoa){
		try {
			parteProcessoManager.incluir(processo, tipoPessoa);
			raiseEvent(ParteProcesso.ALTERACAO_ATIVIDADE_PARTE_PROCESSO);
		} catch (PersistenceException cve){
			FacesMessages.instance().clear();
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					"Parte já cadastrada no Processo!");
		}
	}
	
	public void carregaPessoa(String tipoPessoa, String codigo){
		pessoaManager.carregaPessoa(tipoPessoa, codigo);
	}

}
