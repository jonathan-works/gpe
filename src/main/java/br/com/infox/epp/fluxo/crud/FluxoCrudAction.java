package br.com.infox.epp.fluxo.crud;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(FluxoCrudAction.NAME)
public class FluxoCrudAction extends AbstractCrudAction<Fluxo, FluxoManager> {

    private static final long serialVersionUID = 1L;
    private static final String DESCRICAO_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:descricaoFluxoDecoration:descricaoFluxo";
    private static final String COD_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:codFluxoDecoration:codFluxo";
    private static final LogProvider LOG = Logging
            .getLogProvider(FluxoCrudAction.class);
    public static final String NAME = "fluxoCrudAction";

    private boolean replica = false;

    public String criarReplica() {
        this.replica = true;
        if (!verificarReplica()) {
            return null;
        }
        final Fluxo fluxo = getInstance();
        fluxo.setPublicado(false);
        getManager().detach(fluxo);
        fluxo.setIdFluxo(null);
        setId(null);
        final String ret = save();
        if (AbstractAction.PERSISTED.equals(ret)) {
            this.replica = false;
        }
        return ret;
    }

    private boolean verificarReplica() {
        final boolean existeFluxoComCodigo = getManager().existeFluxoComCodigo(
                getInstance().getCodFluxo());
        final boolean existeFluxoComDescricao = getManager()
                .existeFluxoComDescricao(getInstance().getFluxo());

        if (existeFluxoComCodigo) {
            final FacesMessage message = FacesMessages.createFacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "#{infoxMessages['fluxo.codigoDuplicado']}");
            FacesContext.getCurrentInstance().addMessage(
                    FluxoCrudAction.COD_FLUXO_COMPONENT_ID, message);
        }
        if (existeFluxoComDescricao) {
            final FacesMessage message = FacesMessages.createFacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "#{infoxMessages['fluxo.descricaoDuplicada']}");
            FacesContext.getCurrentInstance().addMessage(
                    FluxoCrudAction.DESCRICAO_FLUXO_COMPONENT_ID, message);
        }

        return !existeFluxoComCodigo && !existeFluxoComDescricao;
    }

    @Override
    protected boolean isInstanceValid() {
        final Fluxo fluxo = getInstance();
        final Date dataFimPublicacao = fluxo.getDataFimPublicacao();
        final Date dataInicioPublicacao = fluxo.getDataInicioPublicacao();
        final boolean instanceValid = (dataInicioPublicacao != null)
                && ((dataFimPublicacao == null) || !dataFimPublicacao
                        .before(dataInicioPublicacao));
        if (!instanceValid) {
            getMessagesHandler().add(ERROR,
                    "#{infoxMessages['fluxo.dataPublicacaoErrada']}");
        }
        return instanceValid;
    }

    @Override
    public String inactive(final Fluxo fluxo) {
        setInstanceId(fluxo.getIdFluxo());
        if (!getManager().existemProcessosAssociadosAFluxo(fluxo)) {
            return super.inactive(fluxo);
        } else {
            final String message = "#{infoxMessages['fluxo.remocaoProibida']}";
            FluxoCrudAction.LOG.error(message);
            getMessagesHandler().add(ERROR, message);
        }
        newInstance();
        return null;
    }

    public boolean isReplica() {
        return this.replica;
    }

    @Override
    public void newInstance() {
        super.newInstance();
        getInstance().setPublicado(false);
        this.replica = false;
    }
}
