package br.com.infox.epp.processo.consulta.action;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoPermissaoManager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;

@Name(ConsultaController.NAME)
public class ConsultaController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaController";
    
    private ProcessoEpa processoEpa;
    @In private ProcessoEpaManager processoEpaManager;
    @In private SigiloDocumentoPermissaoManager sigiloDocumentoPermissaoManager;
    
    @Override
    public void setId(Object id) {
        this.setProcessoEpa(processoEpaManager.find(Integer.valueOf((String)id)));
        super.setId(id);
        
    }

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
    }

    public List<ProcessoDocumento> getProcessoDocumentoList() {
        return sigiloDocumentoPermissaoManager.getDocumentosPermitidos(processoEpa, Authenticator.getUsuarioLogado());
    }
}
