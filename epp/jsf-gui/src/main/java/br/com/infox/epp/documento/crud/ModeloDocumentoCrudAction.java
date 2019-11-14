package br.com.infox.epp.documento.crud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.HistoricoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.list.HistoricoModeloDocumentoList;
import br.com.infox.epp.documento.manager.HistoricoModeloDocumentoManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoPapelManager;
import br.com.infox.epp.documento.manager.VariavelManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class ModeloDocumentoCrudAction extends AbstractCrudAction<ModeloDocumento, ModeloDocumentoManager> {

    private static final long serialVersionUID = 1L;

    private static final LogProvider LOG = Logging.getLogProvider(ModeloDocumentoCrudAction.class);

    private ModeloDocumento modeloDocumentoAnterior;

    @Inject
    private VariavelManager variavelManager;
    @Inject
    private TipoModeloDocumentoPapelManager tipoModeloDocumentoPapelManager;
    @Inject
    private HistoricoModeloDocumentoManager historicoModeloDocumentoManager;
    @Inject
    private ModeloDocumentoManager modeloDocumentoManager;
    @Inject
    private HistoricoModeloDocumentoCrudAction historicoModeloDocumentoCrudAction;
    @Inject
    private HistoricoModeloDocumentoList historicoModeloDocumentoList;

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

    public void onClickHistoricoTab() {
        historicoModeloDocumentoCrudAction.setModeloDocumento(getInstance());
        historicoModeloDocumentoList.getEntity().setModeloDocumento(getInstance());
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

    public void reloadAfterRestaurar() {
        getManager().refresh(getInstance());
        updateOldInstance();
        setTab(TAB_FORM);
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

    @Override
    protected ModeloDocumentoManager getManager() {
        return modeloDocumentoManager;
    }
}
