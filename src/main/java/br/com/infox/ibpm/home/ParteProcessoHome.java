package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epa.manager.ParteProcessoManager;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.itx.component.AbstractHome;

@Name(ParteProcessoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ParteProcessoHome extends AbstractHome<ParteProcesso>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parteProcessoHome";
	
	public static final String ALTERACAO_ATIVIDADE_PARTE_PROCESSO = "parteProcessoHomeAlternaAtividadeParteProcesso";
	
	private String motivoModificacao;
	
	@In private ParteProcessoManager parteProcessoManager;
	
	@Override
    public void newInstance() {
	    motivoModificacao = "";
        super.newInstance();
    }

    public void alternarAtividadeParteProcesso(){
		parteProcessoManager.alternarAtividade(getInstance(), motivoModificacao);
		newInstance();
		raiseEvent(ALTERACAO_ATIVIDADE_PARTE_PROCESSO);
	}

	public String getMotivoModificacao() {
		return motivoModificacao;
	}

	public void setMotivoModificacao(String motivoModificacao) {
		this.motivoModificacao = motivoModificacao;
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		raiseEvent(ALTERACAO_ATIVIDADE_PARTE_PROCESSO);
		return super.afterPersistOrUpdate(ret);
	}
}
