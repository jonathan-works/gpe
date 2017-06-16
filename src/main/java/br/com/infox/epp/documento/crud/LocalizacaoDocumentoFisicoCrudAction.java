package br.com.infox.epp.documento.crud;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.component.tree.LocalizacaoFisicaTreeHandler;
import br.com.infox.epp.documento.entity.DocumentoFisico;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;
import br.com.infox.epp.documento.list.LocalizacaoFisicaList;
import br.com.infox.epp.documento.manager.DocumentoFisicoManager;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class LocalizacaoDocumentoFisicoCrudAction extends AbstractCrudAction<DocumentoFisico, DocumentoFisicoManager> {
	
    private static final long serialVersionUID = 1L;

    @Inject
    private DocumentoFisicoManager documentoFisicoManager;

    private List<LocalizacaoFisica> localizacaoFisicaList;
    private List<DocumentoFisico> documentoFisicoList;
    private Processo processo;

    public void setProcesso(Processo processo) {
        this.processo = processo;
        this.localizacaoFisicaList = Beans.getReference(LocalizacaoFisicaList.class).getResultList();
        listByProcesso();
    }

    @Override
    protected boolean isInstanceValid() {
        getInstance().setProcesso(processo);
        return super.isInstanceValid();
    }

    @Override
    protected void afterSave() {
        newInstance();
        listByProcesso();
        Beans.getReference(LocalizacaoFisicaTreeHandler.class).clearTree();
    }
    
    @Override
    public String remove(DocumentoFisico obj) {
        getDocumentoFisicoList().remove(obj);
        return super.remove(obj);
    };

    @Override
    public String inactive(final DocumentoFisico obj) {
        final String inactive = super.inactive(obj);
        if (inactive != null) {
            getDocumentoFisicoList().remove(obj);
        }
        return inactive;
    }

    private void listByProcesso() {
        setDocumentoFisicoList(getManager().listByProcesso(processo));
    }

    public List<LocalizacaoFisica> getLocalizacaoFisicaList() {
        return localizacaoFisicaList;
    }

    public void setLocalizacaoFisicaList(List<LocalizacaoFisica> localizacaoFisicaList) {
        this.localizacaoFisicaList = localizacaoFisicaList;
    }

    public void setDocumentoFisicoList(
            final List<DocumentoFisico> documentoFisicoList) {
        this.documentoFisicoList = documentoFisicoList;
    }

    public List<DocumentoFisico> getDocumentoFisicoList() {
        return documentoFisicoList;
    }

    @Override
    protected DocumentoFisicoManager getManager() {
        return documentoFisicoManager;
    }
}
