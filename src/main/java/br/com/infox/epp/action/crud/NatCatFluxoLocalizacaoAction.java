package br.com.infox.epp.action.crud;

import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.list.NatCatFluxoLocalizacaoList;
import br.com.infox.epp.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(NatCatFluxoLocalizacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NatCatFluxoLocalizacaoAction extends AbstractHome<NatCatFluxoLocalizacao> {

	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/NatCatFluxoLocalizacao/NatCatFluxoLocalizacaoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "NatCatFluxoLoc.xls";

	public static final String NAME = "natCatFluxoLocalizacaoAction";

	@In
	private NatCatFluxoLocalizacaoManager natCatFluxoLocalizacaoManager;
	private LocalizacaoTreeHandler localizacaoTreeHandler = new LocalizacaoTreeHandler();
	
	@Override
	public EntityList<NatCatFluxoLocalizacao> getBeanList() {
		return NatCatFluxoLocalizacaoList.instance();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	@Override
	public String persist() {
		String result = null;
		try {
			if(getInstance().getHeranca()) {
				natCatFluxoLocalizacaoManager.persistWithChildren(getInstance());
				result = PERSISTED;
			} else {
				natCatFluxoLocalizacaoManager.persist(getInstance());
				result = PERSISTED;
			}
			newInstance();
			FacesMessages.instance().add("Registro incluido com sucesso!");
		} catch (EntityExistsException e) {
			e.printStackTrace();
			FacesMessages.instance().add(Severity.ERROR, "Registro já Cadastrado!");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			FacesMessages.instance().add(Severity.ERROR, "Falha de consistência!");
		} catch (TransactionRequiredException e) {
			e.printStackTrace();
			FacesMessages.instance().add(Severity.ERROR, "Falha de transação!");
		}
		return result;
	}
	
	@Override
	public String remove(NatCatFluxoLocalizacao obj) {
		final String result = super.remove(obj);
		newInstance();
		return result;
	}
	
	public void setLocalizacaoTreeHandler(LocalizacaoTreeHandler localizacaoTreeHandler) {
		this.localizacaoTreeHandler = localizacaoTreeHandler;
	}

	public LocalizacaoTreeHandler getLocalizacaoTreeHandler() {
		return localizacaoTreeHandler;
	}

}