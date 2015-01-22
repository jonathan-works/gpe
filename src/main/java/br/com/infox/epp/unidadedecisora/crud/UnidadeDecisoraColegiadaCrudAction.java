package br.com.infox.epp.unidadedecisora.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiadaMonocratica;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaMonocraticaManager;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(UnidadeDecisoraColegiadaCrudAction.NAME)
public class UnidadeDecisoraColegiadaCrudAction extends AbstractCrudAction<UnidadeDecisoraColegiada, UnidadeDecisoraColegiadaManager>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaCrudAction";
	
	@In
	private UnidadeDecisoraMonocraticaManager unidadeDecisoraMonocraticaManager;
	@In
	private UnidadeDecisoraColegiadaMonocraticaManager unidadeDecisoraColegiadaMonocraticaManager; 
	
	private UnidadeDecisoraColegiadaMonocratica unidadeDecisoraColegiadaMonocratica;
	private List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaItemList;
	
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
	
	@Override
	public String save() {
		String ret = super.save();
		if (GenericDatabaseErrorCode.UNIQUE_VIOLATION.name().equals(ret)){
			Integer idLocalizacao = getInstance().getLocalizacao().getIdLocalizacao();
			boolean existeLoc = getManager().existeUnidadeColegiadaComLocalizacao(idLocalizacao);
			if (existeLoc){
				FacesMessages.instance().clearGlobalMessages();
				FacesMessages.instance().add("#{eppmessages['unidadeDecisoraColegiada.jaExisteLocalizacao']}");
			}
		}
		return ret;
	}
	
	@Override
	protected boolean isInstanceValid() {
		boolean existeLoc = unidadeDecisoraMonocraticaManager.existeUnidadeMonocraticaComLocalizacao(getInstance().getLocalizacao().getIdLocalizacao());
		if(existeLoc){
			getMessagesHandler().add("#{eppmessages['unidadeDecisoraMonocratica.jaExisteLocalizacao']}");
			return false;
		}
		return super.isInstanceValid();
	}
	
	public void adicionarUnidadeMonocratica() {
		try {	
			unidadeDecisoraColegiadaMonocratica.setUnidadeDecisoraColegiada(getInstance());
			unidadeDecisoraColegiadaMonocraticaManager.persist(unidadeDecisoraColegiadaMonocratica);
			getInstance().getUnidadeDecisoraColegiadaMonocraticaList().add(unidadeDecisoraColegiadaMonocratica);
			FacesMessages.instance().add(MSG_REGISTRO_CRIADO);
			unidadeDecisoraColegiadaMonocratica = new UnidadeDecisoraColegiadaMonocratica();
		} catch (DAOException e) {
			FacesMessages.instance().add(e.getMessage());
		}
	}
	
	public void removerUnidadeDemocratica(UnidadeDecisoraColegiadaMonocratica unidadeDecisoraColegiadaMonocratica){
		getInstance().getUnidadeDecisoraColegiadaMonocraticaList().remove(unidadeDecisoraColegiadaMonocratica);
		FacesMessages.instance().add(MSG_REGISTRO_REMOVIDO);
	}
	
	public boolean jaPosuiPresidente(){
		for (UnidadeDecisoraColegiadaMonocratica decisoraColegiadaMonocratica : getInstance().getUnidadeDecisoraColegiadaMonocraticaList()) {
			if (decisoraColegiadaMonocratica.getPresidente()){
				return true;
			}
		}
		return false;
	}

	public List<UnidadeDecisoraMonocratica> getUnidadeDecisoraMonocraticaItemList() {
		unidadeDecisoraMonocraticaItemList = unidadeDecisoraMonocraticaManager.getListUnidadeDecisoraMonocraticaWithIdColegiada((Integer) getId());
		return unidadeDecisoraMonocraticaItemList;
	}

	public void setUnidadeDecisoraMonocraticaItemList(List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaItemList) {
		this.unidadeDecisoraMonocraticaItemList = unidadeDecisoraMonocraticaItemList;
	}

	public UnidadeDecisoraColegiadaMonocratica getUnidadeDecisoraColegiadaMonocratica() {
		if (unidadeDecisoraColegiadaMonocratica == null){
			unidadeDecisoraColegiadaMonocratica = new UnidadeDecisoraColegiadaMonocratica();
		}
		return unidadeDecisoraColegiadaMonocratica;
	}

	public void setUnidadeDecisoraColegiadaMonocratica(UnidadeDecisoraColegiadaMonocratica unidadeDecisoraColegiadaMonocratica) {
		this.unidadeDecisoraColegiadaMonocratica = unidadeDecisoraColegiadaMonocratica;
	}
	
}
