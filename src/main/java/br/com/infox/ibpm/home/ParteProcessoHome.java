package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.manager.ParteProcessoManager;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.itx.component.AbstractHome;

@Name(ParteProcessoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ParteProcessoHome extends AbstractHome<ParteProcesso>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parteProcessoHome";
	private static final Log LOG = Logging.getLog(ParteProcessoHome.class);
	
	private String motivoModificacao;
	
	@In private ParteProcessoManager parteProcessoManager;
	
	@Override
    public void newInstance() {
	    motivoModificacao = "";
        super.newInstance();
    }

    public void alternarAtividadeParteProcesso(){
		try {
			parteProcessoManager.alternarAtividade(getInstance(), motivoModificacao);
			newInstance();
			raiseEvent(ParteProcesso.ALTERACAO_ATIVIDADE_PARTE_PROCESSO);
		} catch (DAOException e) {
			LOG.error(".alternarAtividadeParteProcesso()", e);
			FacesMessages.instance().add(e.getLocalizedMessage());
		}
	}

	public String getMotivoModificacao() {
		return motivoModificacao;
	}

	public void setMotivoModificacao(String motivoModificacao) {
		this.motivoModificacao = motivoModificacao;
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		raiseEvent(ParteProcesso.ALTERACAO_ATIVIDADE_PARTE_PROCESSO);
		return super.afterPersistOrUpdate(ret);
	}
}
