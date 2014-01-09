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
import br.com.itx.util.EntityUtil;

@Name(FluxoCrudAction.NAME)
public class FluxoCrudAction extends AbstractCrudAction<Fluxo> {

	public static final String NAME = "fluxoCrudAction";
    private static final String DESCRICAO_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:descricaoFluxoDecoration:descricaoFluxo";
	private static final String COD_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:codFluxoDecoration:codFluxo";
    private static final LogProvider LOG = Logging.getLogProvider(FluxoCrudAction.class);
    
    @In private FluxoManager fluxoManager;
    
    private boolean replica = false;
    
    public String criarReplica() {
    	this.replica = true;
    	if (!verificarReplica()) {
    		return null;
    	}
        Fluxo fluxo = getInstance();
        fluxoManager.detach(fluxo);
        fluxo.setIdFluxo(null);
        setId(null);
        String ret = save();
        if (PERSISTED.equals(ret)) {
        	this.replica = false;
        }
        return ret;
    }

	private boolean verificarReplica() {
		boolean existeFluxoComCodigo = fluxoManager.existeFluxoComCodigo(getInstance().getCodFluxo());
		boolean existeFluxoComDescricao = fluxoManager.existeFluxoComDescricao(getInstance().getFluxo());
		
		if (existeFluxoComCodigo) {
			FacesMessage message = FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR, "#{messages['fluxo.codigoDuplicado']}");
			FacesContext.getCurrentInstance().addMessage(COD_FLUXO_COMPONENT_ID, message);
		}
		if (existeFluxoComDescricao) {
			FacesMessage message = FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR, "#{messages['fluxo.descricaoDuplicada']}");
			FacesContext.getCurrentInstance().addMessage(DESCRICAO_FLUXO_COMPONENT_ID, message);
		}
		
		return !existeFluxoComCodigo && !existeFluxoComDescricao;
	}
    
    @Override
    protected boolean beforeSave() {
        Date dataFimPublicacao = getInstance().getDataFimPublicacao();
        if (isValidaDataFimPublicacao(dataFimPublicacao)){
            FacesMessages.instance().add(Severity.ERROR, "#{messages['fluxo.dataPublicacaoErrada']}");
            return Boolean.FALSE;
        }
        
        verificaPublicacao();
        return super.beforeSave();
    }
    
    private boolean isValidaDataFimPublicacao(Date dataFimPublicacao) {
        return dataFimPublicacao != null && dataFimPublicacao.before(getInstance().getDataInicioPublicacao());
    }
    
    private void verificaPublicacao(){
        Date data = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String dataHoje = formato.format(data);
        String dataInicio = formato.format(getInstance().getDataInicioPublicacao());
        
        if (dataHoje.equals(dataInicio)){
            getInstance().setPublicado(Boolean.TRUE);
        }
    }
    
    @Override
    public String save() {
        try {
            String ret = super.save();
            UsuarioLogin usuarioPublicacao = getInstance().getUsuarioPublicacao();
            if (usuarioPublicacao != null) {
                List<Fluxo> usuarioPublicacaoList = usuarioPublicacao
                        .getFluxoList();
                if (!usuarioPublicacaoList.contains(getInstance())) {
                    EntityUtil.getEntityManager().refresh(usuarioPublicacao);
                }
            }
            return ret;
        } catch (Exception e){
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public String inactive(Fluxo fluxo) {
        setInstance((Fluxo) fluxo);
        if (!fluxoManager.existemProcessosAssociadosAFluxo(fluxo)) {
            String ret = super.inactive(fluxo);
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
