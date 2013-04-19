package br.com.infox.epa.action.crud;

import java.util.Iterator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epa.entity.Natureza;
import br.com.infox.epa.entity.NaturezaLocalizacao;
import br.com.infox.epa.manager.NaturezaLocalizacaoManager;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(NaturezaLocalizacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaLocalizacaoAction extends AbstractHome<NaturezaLocalizacao> {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "naturezaLocalizacaoAction";

	@In
	private NaturezaLocalizacaoManager naturezaLocalizacaoManager; 
	
	private LocalizacaoTreeHandler localizacaoTreeHandler = new LocalizacaoTreeHandler();
	private List<NaturezaLocalizacao> naturezaLocalizacaoList;
	private Natureza natureza;
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setNatureza(natureza);
		return super.beforePersistOrUpdate();
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		newInstance();
		listByNatureza();
		return ret;
	}
	
	@Override
	public String remove(NaturezaLocalizacao obj) {
		String remove = super.remove(obj);
		if(remove != null) {
			naturezaLocalizacaoList.remove(obj);
		}
		return remove;
	}

	public void removeAll() {
		for (Iterator<NaturezaLocalizacao> iterator = naturezaLocalizacaoList.iterator(); iterator.hasNext();) {
			NaturezaLocalizacao nl = iterator.next();
			getEntityManager().remove(nl);
			iterator.remove();
		}
		FacesMessages.instance().add("Registros removidos com sucesso!");
	}
		
	public void init() {
		NaturezaAction naturezaAction = (NaturezaAction) Component.getInstance(NaturezaAction.NAME);
		natureza = naturezaAction.getInstance();
		listByNatureza();
	}

	private void listByNatureza() {
		naturezaLocalizacaoList = naturezaLocalizacaoManager.listByNatureza(natureza);
	}	

	public void setLocalizacaoTreeHandler(LocalizacaoTreeHandler localizacaoTreeHandler) {
		this.localizacaoTreeHandler = localizacaoTreeHandler;
	}

	public LocalizacaoTreeHandler getLocalizacaoTreeHandler() {
		return localizacaoTreeHandler;
	}

	public void setNaturezaLocalizacaoList(List<NaturezaLocalizacao> naturezaLocalizacaoList) {
		this.naturezaLocalizacaoList = naturezaLocalizacaoList;
	}

	public List<NaturezaLocalizacao> getNaturezaLocalizacaoList() {
		return naturezaLocalizacaoList;
	}
	
}