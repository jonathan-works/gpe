package br.com.infox.epp.unidadedecisora.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(UnidadeDecisoraMonocraticaCrudAction.NAME)
public class UnidadeDecisoraMonocraticaCrudAction extends AbstractCrudAction<UnidadeDecisoraMonocratica, UnidadeDecisoraMonocraticaManager>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaCrudAction";
	
	@Override
	public void newInstance() {
		super.newInstance();
		LocalizacaoTreeHandler tree = ComponentUtil.getComponent(LocalizacaoTreeHandler.NAME);
		tree.clearTree();
	}
	
	public Localizacao getLocalizacao() {
	    return getInstance().getLocalizacao();
	}
	
	public void setLocalizacao(Localizacao localizacao) {
	    if (localizacao == null || localizacao.getEstruturaFilho() != null) {
	        getInstance().setLocalizacao(localizacao);
	    } 
	}
	
}
