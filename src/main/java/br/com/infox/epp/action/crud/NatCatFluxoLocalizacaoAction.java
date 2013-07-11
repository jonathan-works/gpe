package br.com.infox.epp.action.crud;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.entity.NaturezaCategoriaFluxo;
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
	private static final String	LOG_MESSAGE_PERSIST	= "NatCatFluxoLocalizacaoAction.persist()";
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/NatCatFluxoLocalizacao/NatCatFluxoLocalizacaoTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "NatCatFluxoLoc.xls";
	private static final LogProvider LOG = Logging.getLogProvider(NatCatFluxoLocalizacaoAction.class);

	public static final String NAME = "natCatFluxoLocalizacaoAction";

	@In
	private NatCatFluxoLocalizacaoManager natCatFluxoLocalizacaoManager;
	private LocalizacaoTreeHandler localizacaoTreeHandler = new LocalizacaoTreeHandler();
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private List<NatCatFluxoLocalizacao> natCatFluxolocalizacaoList;
	private boolean updateList = true;
	
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
	public void newInstance() {
		super.newInstance();
		updateList = true;
	}
	
	@Override
	public String persist() {
		String result = null;
		try {
			localizacaoTreeHandler.clearTree();
			getInstance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
			if(getInstance().getHeranca()) {
				natCatFluxoLocalizacaoManager.persistWithChildren(getInstance());
			} else {
				natCatFluxoLocalizacaoManager.persist(getInstance());
			}
			result = PERSISTED;
			newInstance();
			FacesMessages.instance().add("Registro incluso com sucesso!");
		} catch (EntityExistsException e) {
			LOG.info(LOG_MESSAGE_PERSIST, e);
			FacesMessages.instance().add(Severity.INFO, "Registro já Cadastrado!");
		} catch (IllegalArgumentException e) {
			LOG.error(LOG_MESSAGE_PERSIST, e);
			FacesMessages.instance().add(Severity.ERROR, "Falha de consistência!");
		} catch (TransactionRequiredException e) {
			LOG.error(LOG_MESSAGE_PERSIST, e);
			FacesMessages.instance().add(Severity.ERROR, "Falha de transação!");
		} catch (Exception e) {
			LOG.error(LOG_MESSAGE_PERSIST, e);
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

	
	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}

	
	public void setNaturezaCategoriaFluxo(
			NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
		newInstance();
	}

	
	public List<NatCatFluxoLocalizacao> getNatCatFluxolocalizacaoList() {
		if (updateList) {
			this.natCatFluxolocalizacaoList = natCatFluxoLocalizacaoManager.listByNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
			updateList = false;
		}
		return natCatFluxolocalizacaoList;
	}

	
	public void setNatCatFluxolocalizacaoList(
			List<NatCatFluxoLocalizacao> natCatFluxolocalizacaoList) {
		this.natCatFluxolocalizacaoList = natCatFluxolocalizacaoList;
	}

}