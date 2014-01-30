package br.com.infox.epp.documento.crud;

import java.util.Iterator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.component.tree.LocalizacaoFisicaTreeHandler;
import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;
import br.com.infox.epp.documento.list.LocalizacaoFisicaList;
import br.com.infox.epp.documento.manager.DocumentoFisicoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.home.ProcessoHome;

@Name(LocalizacaoDocumentoFisicoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class LocalizacaoDocumentoFisicoCrudAction extends
		AbstractCrudAction<DocumentoFisico> {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(LocalizacaoDocumentoFisicoCrudAction.class);

	public static final String NAME = "localizacaoDocumentoFisicoCrudAction";

	private List<LocalizacaoFisica> localizacaoFisicaList;
	private List<DocumentoFisico> documentoFisicoList;
	private Processo processo;

	@In
	private DocumentoFisicoManager documentoFisicoManager;

	@Override
	protected boolean isInstanceValid() {
		getInstance().setProcesso(processo);
		return super.isInstanceValid();
	}

	@Override
	protected void afterSave() {
		newInstance();
		listByProcesso();
		final LocalizacaoFisicaTreeHandler tree = (LocalizacaoFisicaTreeHandler) Component.getInstance(LocalizacaoFisicaTreeHandler.NAME);
		tree.clearTree();
	}

	@Override
	public String inactive(final DocumentoFisico obj) {
		final String inactive = super.inactive(obj);
		if (inactive != null) {
			getDocumentoFisicoList().remove(obj);
		}
		return inactive;
	}
	
	public void inactiveAll() {
		for (final Iterator<DocumentoFisico> iterator = getDocumentoFisicoList()
				.iterator(); iterator.hasNext();) {
			final DocumentoFisico nl = iterator.next();
			try {
				nl.setAtivo(false);
				getGenericManager().update(nl);
			} catch (final Exception e) {
			    LOG.error(".inactiveAll()", e);
			}
			iterator.remove();
		}
		FacesMessages.instance().add("Registros inativados com sucesso!");
	}

	public void init() {
		final ProcessoHome processoHome = ProcessoHome.instance();
		processo = processoHome.getInstance();
		localizacaoFisicaList = new LocalizacaoFisicaList().getResultList();
		listByProcesso();
	}

	private void listByProcesso() {
		setDocumentoFisicoList(documentoFisicoManager.listByProcesso(processo));
	}

	public List<LocalizacaoFisica> getLocalizacaoFisicaList() {
		return localizacaoFisicaList;
	}

	public void setLocalizacaoFisicaList(
			final List<LocalizacaoFisica> localizacaoFisicaList) {
		this.localizacaoFisicaList = localizacaoFisicaList;
	}

	public void setDocumentoFisicoList(final List<DocumentoFisico> documentoFisicoList) {
		this.documentoFisicoList = documentoFisicoList;
	}

	public List<DocumentoFisico> getDocumentoFisicoList() {
		return documentoFisicoList;
	}

}