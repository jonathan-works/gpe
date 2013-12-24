package br.com.infox.epp.documento.crud;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.HistoricoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.HistoricoModeloDocumentoManager;

@Name(HistoricoModeloDocumentoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class HistoricoModeloDocumentoCrudAction extends AbstractCrudAction<HistoricoModeloDocumento> {

    public static final String NAME = "historicoModeloDocumentoCrudAction";
    
    @In HistoricoModeloDocumentoManager historicoModeloDocumentoManager;
    
    private HistoricoModeloDocumento selecionado;
    
    private List<ModeloDocumento> modeloDocumentoList;
    private List<UsuarioLogin> usuarioAlteracaoList;
    
    public HistoricoModeloDocumento getSelecionado() {
        return selecionado;
    }
    
    public void setSelecionado(HistoricoModeloDocumento selecionado) {
        this.selecionado = selecionado;
    }
    
    public void setModeloDocumento(ModeloDocumento modeloDocumento) {
        setModeloDocumentoList(historicoModeloDocumentoManager.listModelosDoHistorico());
        setUsuarioAlteracaoList(historicoModeloDocumentoManager.listUsuariosQueAlteraramModelo(modeloDocumento));
    }
    
    public List<UsuarioLogin> getUsuarioAlteracaoList() {
        return usuarioAlteracaoList;
    }

    public void setUsuarioAlteracaoList(List<UsuarioLogin> usuarioAlteracaoList) {
        this.usuarioAlteracaoList = usuarioAlteracaoList;
    }
    
    public List<ModeloDocumento> getModeloDocumentoList() {
        return modeloDocumentoList;
    }

    public void setModeloDocumentoList(
            List<ModeloDocumento> historicoModeloDocumentoList) {
        this.modeloDocumentoList = historicoModeloDocumentoList;
    }
    
}
