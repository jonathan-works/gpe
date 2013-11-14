package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.entity.ProcessoEpa;
import br.com.infox.epp.manager.ParteProcessoManager;
import br.com.infox.ibpm.action.ProcessoEpaAction;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.manager.PessoaManager;
import br.com.itx.component.AbstractHome;

@Name(ProcessoEpaHome.NAME)
@Scope(ScopeType.PAGE)
public class ProcessoEpaHome extends AbstractHome<ProcessoEpa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoEpaHome";
	private static final Log LOG = Logging.getLog(ProcessoEpaAction.class);
	
	@In private PessoaManager pessoaManager;
	@In private ParteProcessoManager parteProcessoManager;
	
	public void incluirParteProcesso(Processo processo, String tipoPessoa){
		try {
			parteProcessoManager.incluir(processo, tipoPessoa);
			raiseEvent(ParteProcesso.ALTERACAO_ATIVIDADE_PARTE_PROCESSO);
		} catch (DAOException e){
			LOG.error(".incluirParteProcesso()", e);
			FacesMessages.instance().clear();
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, e.getLocalizedMessage());
		}
	}
	
	public void carregaPessoa(String tipoPessoa, String codigo){
		pessoaManager.carregaPessoa(tipoPessoa, codigo);
	}

}
