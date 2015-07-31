package br.com.infox.epp.processo.consulta.action;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.action.DocumentoProcessoAction;
import br.com.infox.epp.processo.documento.action.PastaAction;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.list.PastaList;
import br.com.infox.epp.processo.documento.manager.PastaRestricaoAction;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoPermissaoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;

@AutoCreate
@Name(ConsultaController.NAME)
public class ConsultaController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaController";
    
    @In
    private ProcessoManager processoManager;
    @In
    private SigiloDocumentoPermissaoManager sigiloDocumentoPermissaoManager;
    @In
    private SigiloProcessoService sigiloProcessoService;
    @In
    private MetadadoProcessoManager metadadoProcessoManager;
    @In
    private PastaAction pastaAction;
    @In
    private DocumentoList documentoList;
    @In
    private DocumentoProcessoAction documentoProcessoAction;
    @In
    private PastaRestricaoAction pastaRestricaoAction;
    @In
    private PastaList pastaList;
    
    
    private Processo processo;
    private boolean showAllDocuments = false;
    private List<MetadadoProcesso> detalhesMetadados;
    private boolean showBackButton = true;

	public boolean isShowBackButton() {
		return showBackButton;
	}

	public void setShowBackButton(boolean showBackButton) {
		this.showBackButton = showBackButton;
	}

	public boolean isShowAllDocuments() {
        return showAllDocuments;
    }

    public void setShowAllDocuments(boolean showAllDocuments) {
        this.showAllDocuments = showAllDocuments;
    }

    @Override
    public void setId(Object id) {
    	if (id instanceof String) {
    		id = Integer.valueOf((String) id);
    	}
    	Processo processo = processoManager.find(id);
    	if (processo == null || processo.getProcessoPai() == null) {
    		this.setProcesso(processo);
    		super.setId(id);
    	} else {
    		this.setProcesso(null);
    		super.setId(null);
    	}
    }
    
    public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public List<Documento> getProcessoDocumentoList(Long idTask) {
        List<Documento> list = sigiloDocumentoPermissaoManager.getDocumentosPermitidos(processo, Authenticator.getUsuarioLogado());
        list = filtrarPorTarefa(list, idTask);
        return filtrarAnexos(list);
    }

    private List<Documento> filtrarPorTarefa(List<Documento> list, Long taskId) {
        if (!showAllDocuments && taskId != null) {
            List<Documento> ret = new ArrayList<Documento>();
            for (Documento documento : list) {
                if (taskId.equals(documento.getIdJbpmTask())) {
                    ret.add(documento);
                }
            }
            return ret;
        }
        return list;
    }

    private List<Documento> filtrarAnexos(List<Documento> list) {
        List<Documento> ret = new ArrayList<Documento>();
        for (Documento documento : list) {
            if (documento.getAnexo() != null && documento.getAnexo()) {
                ret.add(documento);
            }
        }
        return ret;
    }
    
    public void checarVisibilidade() {
        if (!sigiloProcessoService.usuarioPossuiPermissao(Authenticator.getUsuarioLogado(), processo)) {
            FacesMessages.instance().add("Usuário sem permissão");
            Redirect.instance().setViewId("/error.seam");
            Redirect.instance().setConversationPropagationEnabled(false);
            Redirect.instance().execute();
        }
    }
    
    public List<MetadadoProcesso> getDetalhesMetadados() {
    	if (detalhesMetadados == null) {
    		detalhesMetadados = metadadoProcessoManager.getListMetadadoVisivelByProcesso(getProcesso());
    	}
    	return detalhesMetadados;
    }
    
    public Boolean getShowBackButton() {
		return showBackButton;
	}

	public void setShowBackButton(Boolean showBackButton) {
		this.showBackButton = showBackButton;
	}

	@Override
    public void setTab(String tab) {
        super.setTab(tab);
        if(tab.equals("tabAnexos") || tab.equals("tabAnexar")){
        	pastaAction.setProcesso(this.getProcesso());
        }
        if(tab.equals("tabAnexos")){
        	documentoList.setProcesso(this.getProcesso());
        	documentoProcessoAction.setProcesso(getProcesso());
        	documentoProcessoAction.setListClassificacaoDocumento(null);
        }
        if(tab.equals("tabPastaRestricao")) {
            pastaRestricaoAction.setProcesso(getProcesso().getProcessoRoot());
            pastaList.setProcesso(getProcesso().getProcessoRoot());
        }
    }
    
    
}
