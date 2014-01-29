package br.com.infox.epp.fluxo.crud;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;

@Name(FluxoCrudAction.NAME)
public class FluxoCrudAction extends AbstractCrudAction<Fluxo> {

    private static final long serialVersionUID = 1L;
    private static final String DESCRICAO_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:descricaoFluxoDecoration:descricaoFluxo";
	private static final String COD_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:codFluxoDecoration:codFluxo";
    private static final LogProvider LOG = Logging.getLogProvider(FluxoCrudAction.class);
    public static final String NAME = "fluxoCrudAction";
    
    @In private FluxoManager fluxoManager;
    
    private boolean replica = false;
    
    public String criarReplica() {
    	this.replica = true;
    	if (!verificarReplica()) {
    		return null;
    	}
        final Fluxo fluxo = getInstance();
        fluxoManager.detach(fluxo);
        fluxo.setIdFluxo(null);
        setId(null);
        final String ret = save();
        if (PERSISTED.equals(ret)) {
        	this.replica = false;
        }
        return ret;
    }

	private boolean verificarReplica() {
		final boolean existeFluxoComCodigo = fluxoManager.existeFluxoComCodigo(getInstance().getCodFluxo());
		final boolean existeFluxoComDescricao = fluxoManager.existeFluxoComDescricao(getInstance().getFluxo());
		
		if (existeFluxoComCodigo) {
			final FacesMessage message = FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR, "#{messages['fluxo.codigoDuplicado']}");
			FacesContext.getCurrentInstance().addMessage(COD_FLUXO_COMPONENT_ID, message);
		}
		if (existeFluxoComDescricao) {
			final FacesMessage message = FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR, "#{messages['fluxo.descricaoDuplicada']}");
			FacesContext.getCurrentInstance().addMessage(DESCRICAO_FLUXO_COMPONENT_ID, message);
		}
		
		return !existeFluxoComCodigo && !existeFluxoComDescricao;
	}
    
    @Override
    protected boolean isInstanceValid() {
        final Date dataFimPublicacao = getInstance().getDataFimPublicacao();
        if (isValidaDataFimPublicacao(dataFimPublicacao)){
            FacesMessages.instance().add(Severity.ERROR, "#{messages['fluxo.dataPublicacaoErrada']}");
            return Boolean.FALSE;
        }
        
        verificaPublicacao();
        return super.isInstanceValid();
    }
    
    private boolean isValidaDataFimPublicacao(final Date dataFimPublicacao) {
        return dataFimPublicacao != null && dataFimPublicacao.before(getInstance().getDataInicioPublicacao());
    }
    
    private void verificaPublicacao(){
        final Date data = new Date();
        final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        final String dataHoje = formato.format(data);
        final String dataInicio = formato.format(getInstance().getDataInicioPublicacao());
        
        if (dataHoje.equals(dataInicio)){
            getInstance().setPublicado(Boolean.TRUE);
        }
    }
    
    @Override
    public String save() {
        try {
            final String ret = super.save();
            final UsuarioLogin usuarioPublicacao = getInstance().getUsuarioPublicacao();
            if (usuarioPublicacao != null) {
                final List<Fluxo> usuarioPublicacaoList = usuarioPublicacao
                        .getFluxoList();
                if (!usuarioPublicacaoList.contains(getInstance())) {
                    fluxoManager.refresh(usuarioPublicacao);
                }
            }
            return ret;
        } catch (final Exception e){
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public String inactive(final Fluxo fluxo) {
        setInstance((Fluxo) fluxo);
        if (!fluxoManager.existemProcessosAssociadosAFluxo(fluxo)) {
            final String ret = super.inactive(fluxo);
            newInstance();
            return ret;
        } else {
            final String message = "#{messages['fluxo.remocaoProibida']}";
            LOG.warn(message);
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, message);
            return null;
        }
    }
    
    public boolean isReplica() {
		return replica;
	}
}
