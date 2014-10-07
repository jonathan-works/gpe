package br.com.infox.epp.fluxo.crud;

import static java.lang.Boolean.TRUE;
import static org.jboss.seam.international.StatusMessage.Severity.ERROR;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;

@Name(FluxoCrudAction.NAME)
public class FluxoCrudAction extends AbstractCrudAction<Fluxo, FluxoManager> {

    private static final long serialVersionUID = 1L;
    private static final String DESCRICAO_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:descricaoFluxoDecoration:descricaoFluxo";
    private static final String COD_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:codFluxoDecoration:codFluxo";
    private static final LogProvider LOG = Logging.getLogProvider(FluxoCrudAction.class);
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
        if (PERSISTED.equals(ret)) {
            this.replica = false;
        }
        return ret;
    }

    private boolean verificarReplica() {
        final boolean existeFluxoComCodigo = getManager().existeFluxoComCodigo(getInstance().getCodFluxo());
        final boolean existeFluxoComDescricao = getManager().existeFluxoComDescricao(getInstance().getFluxo());

        if (existeFluxoComCodigo) {
            final FacesMessage message = FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR, "#{eppmessages['fluxo.codigoDuplicado']}");
            FacesContext.getCurrentInstance().addMessage(COD_FLUXO_COMPONENT_ID, message);
        }
        if (existeFluxoComDescricao) {
            final FacesMessage message = FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR, "#{eppmessages['fluxo.descricaoDuplicada']}");
            FacesContext.getCurrentInstance().addMessage(DESCRICAO_FLUXO_COMPONENT_ID, message);
        }

        return !existeFluxoComCodigo && !existeFluxoComDescricao;
    }

    @Override
    protected boolean isInstanceValid() {
        final Fluxo fluxo = getInstance();
        final Date dataFimPublicacao = fluxo.getDataFimPublicacao();
        final Date dataInicioPublicacao = fluxo.getDataInicioPublicacao();
        final boolean instanceValid = dataInicioPublicacao != null
                && (dataFimPublicacao == null || !dataFimPublicacao.before(dataInicioPublicacao));
        if (!instanceValid) {
            getMessagesHandler().add(ERROR, "#{eppmessages['fluxo.dataPublicacaoErrada']}");
        }
        return instanceValid;
    }

    @Override
    protected void beforeSave() {
        final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        final String dataHoje = formato.format(new Date());
        final Fluxo fluxo = getInstance();
        final String dataInicio = formato.format(fluxo.getDataInicioPublicacao());

        if (dataHoje.equals(dataInicio)) {
            fluxo.setPublicado(TRUE);
        } else {
            fluxo.setPublicado(false);
        }
    }

    // TODO: rever como proceder em relação a usuarioPublicado. Esta
    // implementação estava fora de uso
    // @Override
    // public String save() {
    // try {
    // final String ret = super.save();
    // final UsuarioLogin usuarioPublicacao =
    // getInstance().getUsuarioPublicacao();
    // if (usuarioPublicacao != null) {
    // final List<Fluxo> usuarioPublicacaoList = usuarioPublicacao
    // .getFluxoList();
    // if (!usuarioPublicacaoList.contains(getInstance())) {
    // fluxoManager.refresh(usuarioPublicacao);
    // }
    // }
    // return ret;
    // } catch (final Exception e){
    // LOG.error(e.getMessage(), e);
    // return null;
    // }
    // }

    @Override
    public String inactive(final Fluxo fluxo) {
        setInstanceId(fluxo.getIdFluxo());
        if (!getManager().existemProcessosAssociadosAFluxo(fluxo)) {
            return super.inactive(fluxo);
        } else {
            final String message = "#{eppmessages['fluxo.remocaoProibida']}";
            LOG.error(message);
            getMessagesHandler().add(ERROR, message);
        }
        newInstance();
        return null;
    }

    public boolean isReplica() {
        return replica;
    }
    
    @Override
    public void newInstance() {
        super.newInstance();
        replica = false;
    }
}
