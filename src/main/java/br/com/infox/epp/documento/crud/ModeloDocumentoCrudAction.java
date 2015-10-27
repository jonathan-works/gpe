package br.com.infox.epp.documento.crud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.HistoricoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.manager.HistoricoModeloDocumentoManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoPapelManager;
import br.com.infox.epp.documento.manager.VariavelManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(ModeloDocumentoCrudAction.NAME)
public class ModeloDocumentoCrudAction extends AbstractCrudAction<ModeloDocumento, ModeloDocumentoManager> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String NAME = "modeloDocumentoCrudAction";

    private static final LogProvider LOG = Logging.getLogProvider(ModeloDocumentoCrudAction.class);

    private ModeloDocumento modeloDocumentoAnterior;

    @In
    private VariavelManager variavelManager;
    @In
    private TipoModeloDocumentoPapelManager tipoModeloDocumentoPapelManager;
    @In
    private HistoricoModeloDocumentoManager historicoModeloDocumentoManager;

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
    protected void afterSave(String ret) {
        updateOldInstance();
    }

    @Override
    protected void beforeSave() {
        gravarHistorico();
    }

    private void gravarHistorico() {
        if (haModificacoesNoModelo()) {
            HistoricoModeloDocumento historico = new HistoricoModeloDocumento();
            historico.setTituloModeloDocumento(modeloDocumentoAnterior.getTituloModeloDocumento());
            historico.setDescricaoModeloDocumento(modeloDocumentoAnterior.getModeloDocumento());
            historico.setAtivo(modeloDocumentoAnterior.getAtivo());
            historico.setDataAlteracao(new Date());
            historico.setModeloDocumento(getInstance());
            historico.setUsuarioAlteracao((UsuarioLogin) ComponentUtil.getComponent(Authenticator.USUARIO_LOGADO));
            try {
                historicoModeloDocumentoManager.persist(historico);
            } catch (DAOException e) {
                LOG.error(".gravarHistorico()", e);
            }
        }

    }

    private boolean haModificacoesNoModelo() {
        return modeloDocumentoAnterior != null
                && getInstance().hasChanges(modeloDocumentoAnterior);
    }

    public void restaurar(HistoricoModeloDocumento historicoModeloDocumento) {
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
