package br.com.infox.epp.documento.crud;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.HistoricoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.HistoricoModeloDocumentoManager;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class HistoricoModeloDocumentoCrudAction extends AbstractCrudAction<HistoricoModeloDocumento, HistoricoModeloDocumentoManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private HistoricoModeloDocumentoManager historicoModeloDocumentoManager;

    @Getter
    private HistoricoModeloDocumento selecionado;
    @Getter @Setter
    private List<ModeloDocumento> modeloDocumentoList;
    @Getter @Setter
    private List<UsuarioLogin> usuarioAlteracaoList;

    public void restaurar() {
        historicoModeloDocumentoManager.restaurar(selecionado.getModeloDocumento().getIdModeloDocumento(), selecionado);
    }

    public void setIdSelecionado(Integer idSelecionado) {
        this.selecionado = historicoModeloDocumentoManager.find(idSelecionado);
    }

    public void setModeloDocumento(ModeloDocumento modeloDocumento) {
        setModeloDocumentoList(getManager().listModelosDoHistorico());
        setUsuarioAlteracaoList(getManager().listUsuariosQueAlteraramModelo(modeloDocumento));
    }

    @Override
    protected HistoricoModeloDocumentoManager getManager() {
        return historicoModeloDocumentoManager;
    }
}
