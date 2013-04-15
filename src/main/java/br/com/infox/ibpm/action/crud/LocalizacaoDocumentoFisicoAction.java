package br.com.infox.ibpm.action.crud;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.ItemChangeEvent;
import org.richfaces.event.ItemChangeListener;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.ibpm.entity.DocumentoFisico;
import br.com.infox.ibpm.entity.LocalizacaoFisica;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.home.ProcessoHome;
import br.com.infox.ibpm.manager.DocumentoFisicoManager;
import br.com.infox.list.LocalizacaoFisicaList;

@Name(LocalizacaoDocumentoFisicoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class LocalizacaoDocumentoFisicoAction extends
		AbstractCrudAction<DocumentoFisico> implements Serializable, ItemChangeListener {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "localizacaoDocumentoFisicoAction";

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
		System.out.println(processo);
		newInstance();
		listByProcesso();
	}

	@Override
	public String inactive(Object obj) {
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
				genericManager.update(nl);
			} catch (Exception e) {
				e.printStackTrace();
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

	@Override
	public void processItemChange(ItemChangeEvent event) throws AbortProcessingException {
		if ("documentoFisicoTab".equals(event.getNewItemName())) {
			init();
		}
	}
}