package br.com.infox.epp.processo.consulta.action;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoPermissaoManager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;

@Name(ConsultaController.NAME)
public class ConsultaController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaController";

    private ProcessoEpa processoEpa;
    
    @In
    private ProcessoEpaManager processoEpaManager;
    @In
    private SigiloDocumentoPermissaoManager sigiloDocumentoPermissaoManager;
    @In
    private SigiloProcessoService sigiloProcessoService;

    private boolean showAllDocuments = false;

    public boolean isShowAllDocuments() {
        return showAllDocuments;
    }

    public void setShowAllDocuments(boolean showAllDocuments) {
        this.showAllDocuments = showAllDocuments;
    }

    @Override
    public void setId(Object id) {
        this.setProcessoEpa(processoEpaManager.find(Integer.valueOf((String) id)));
        super.setId(id);

    }

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
    }

    public List<Documento> getProcessoDocumentoList(Long idTask) {
        List<Documento> list = sigiloDocumentoPermissaoManager.getDocumentosPermitidos(processoEpa, Authenticator.getUsuarioLogado());
        list = filtrarPorTarefa(list, idTask);
        return filtrarAnexos(list);
    }

    private List<Documento> filtrarPorTarefa(
            List<Documento> list, Long taskId) {
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
        if (!sigiloProcessoService.usuarioPossuiPermissao(Authenticator.getUsuarioLogado(), processoEpa)) {
            FacesMessages.instance().add("Usuário sem permissão");
            Redirect.instance().setViewId("/error.seam");
            Redirect.instance().setConversationPropagationEnabled(false);
            Redirect.instance().execute();
        }
    }
}
