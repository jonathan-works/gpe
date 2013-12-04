package br.com.infox.epp.documento.crud;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;
import br.com.infox.epp.documento.list.LocalizacaoFisicaList;
import br.com.infox.epp.documento.manager.DocumentoFisicoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.home.ProcessoHome;

@Name(LocalizacaoDocumentoFisicoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class LocalizacaoDocumentoFisicoCrudAction extends
		AbstractCrudAction<DocumentoFisico> implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(LocalizacaoDocumentoFisicoCrudAction.class);

	public static final String NAME = "localizacaoDocumentoFisicoCrudAction";

	private List<LocalizacaoFisica> localizacaoFisicaList;
	private List<DocumentoFisico> documentoFisicoList;
	private Processo processo;

	@In
	private DocumentoFisicoManager documentoFisicoManager;

	@Override
	protected boolean beforeSave() {
		getInstance().setProcesso(processo);
		return super.beforeSave();
	}

	@Override
	protected void afterSave() {
		newInstance();
		listByProcesso();
	}

	@Override
	public String inactive(DocumentoFisico obj) {
		String inactive = super.inactive(obj);
		if (inactive != null) {
			getDocumentoFisicoList().remove(obj);
		}
		return inactive;
	}
	
	public void inactiveAll() {
		for (Iterator<DocumentoFisico> iterator = getDocumentoFisicoList()
				.iterator(); iterator.hasNext();) {
			DocumentoFisico nl = iterator.next();
			try {
				nl.setAtivo(false);
				getGenericManager().update(nl);
			} catch (Exception e) {
			    LOG.error(".inactiveAll()", e);
			}
			iterator.remove();
		}
		FacesMessages.instance().add("Registros inativados com sucesso!");
	}

	public void init() {
		ProcessoHome processoHome = ProcessoHome.instance();
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
			List<LocalizacaoFisica> localizacaoFisicaList) {
		this.localizacaoFisicaList = localizacaoFisicaList;
	}

	public void setDocumentoFisicoList(List<DocumentoFisico> documentoFisicoList) {
		this.documentoFisicoList = documentoFisicoList;
	}

	public List<DocumentoFisico> getDocumentoFisicoList() {
		return documentoFisicoList;
	}

}