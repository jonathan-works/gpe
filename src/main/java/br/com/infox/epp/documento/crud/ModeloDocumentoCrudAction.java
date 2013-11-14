package br.com.infox.epp.documento.crud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.HistoricoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoPapelManager;
import br.com.infox.epp.documento.manager.VariavelManager;
import br.com.infox.ibpm.home.Authenticator;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(ModeloDocumentoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ModeloDocumentoCrudAction extends AbstractCrudAction<ModeloDocumento> {
    
    public static final String NAME = "modeloDocumentoCrudAction";
    
    private static final LogProvider LOG = Logging.getLogProvider(ModeloDocumentoCrudAction.class);
    
    private ModeloDocumento modeloDocumentoAnterior;
    
    @In private VariavelManager variavelManager;
    @In private TipoModeloDocumentoPapelManager tipoModeloDocumentoPapelManager;
    
    @Override
    public void newInstance() {
        modeloDocumentoAnterior = null;
        super.newInstance();
    }
    
    @Override
    public void setId(Object id) {
        super.setId(id);
        updateOldInstance();
    }

    private void updateOldInstance() {
        try {
            modeloDocumentoAnterior = (ModeloDocumento) EntityUtil.cloneObject(getInstance(), false);
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error(".updateOldInstance()", e);
        }
    }
    
    @Override
    protected void afterSave() {
        updateOldInstance();
        super.afterSave();
    }
    
    @Override
    public String save() {
        gravarHistorico();
        return super.save();
    }
    
    private void gravarHistorico() {
        if (haModificacoesNoModelo()){
            HistoricoModeloDocumento historico = new HistoricoModeloDocumento();
            historico.setTituloModeloDocumento(modeloDocumentoAnterior.getTituloModeloDocumento());
            historico.setDescricaoModeloDocumento(modeloDocumentoAnterior.getModeloDocumento());
            historico.setAtivo(modeloDocumentoAnterior.getAtivo());
            historico.setDataAlteracao(new Date());
            historico.setModeloDocumento(getInstance());
            historico.setUsuarioAlteracao((UsuarioLogin) ComponentUtil.getComponent(Authenticator.USUARIO_LOGADO));
            persist(historico);
        }
        
    }

    private boolean haModificacoesNoModelo() {
        return modeloDocumentoAnterior != null && getInstance().hasChanges(modeloDocumentoAnterior);
    }
    
    public void restaurar(HistoricoModeloDocumento historicoModeloDocumento){
        if (historicoModeloDocumento == null) {
            return;
        }
        updateOldInstance();
        restaurarAtributos(historicoModeloDocumento);
        save();
    }

    private void restaurarAtributos(
            HistoricoModeloDocumento historicoModeloDocumento) {
        getInstance().setAtivo(historicoModeloDocumento.getAtivo());
        getInstance().setModeloDocumento(historicoModeloDocumento.getDescricaoModeloDocumento());
        getInstance().setTipoModeloDocumento(historicoModeloDocumento.getModeloDocumento().getTipoModeloDocumento());
        getInstance().setTituloModeloDocumento(historicoModeloDocumento.getTituloModeloDocumento());
    }
    
    public List<Variavel> getVariaveis() {
        if (getInstance().getTipoModeloDocumento() != null) {
            return variavelManager.getVariaveisByTipoModeloDocumento(getInstance().getTipoModeloDocumento());
        }
        return new ArrayList<Variavel>();
    }
    
    public List<TipoModeloDocumentoPapel> getTiposModeloDocumentoPermitidos() {
        return tipoModeloDocumentoPapelManager.getTiposModeloDocumentoPermitidos();
    }

}
