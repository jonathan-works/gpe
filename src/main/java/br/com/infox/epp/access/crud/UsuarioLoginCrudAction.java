package br.com.infox.epp.access.crud;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin, UsuarioLoginManager> {
    
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioLoginCrudAction.class);
	private static final long serialVersionUID = 1L;
    
    @Inject
    private UnidadeDecisoraMonocraticaManager unidadeDecisoraMonocraticaManager;
    @Inject
    private UnidadeDecisoraColegiadaManager unidadeDecisoraColegiadaManager;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    
    private List<Map<String, String>> unidadeDecisoraMonocraticaList;
    private List<Map<String, String>> unidadeDecisoraColegiadaList;
    private boolean podeEditarLogin;
    
    public void limparListasDeUnidadesDecisoras() {
    	unidadeDecisoraMonocraticaList = null;
    	unidadeDecisoraColegiadaList = null;
    }

    public UsuarioEnum[] getTiposDeUsuario() {
        return UsuarioEnum.values();
    }
    
    public List<Map<String, String>> getUnidadeDecisoraMonocraticaList(){
    	if (unidadeDecisoraMonocraticaList == null){
    		unidadeDecisoraMonocraticaList = unidadeDecisoraMonocraticaManager.getUnidadeDecisoraListByUsuario((Integer) getInstanceId());
    	}
    	return unidadeDecisoraMonocraticaList;
    }
    
    public List<Map<String, String>> getUnidadeDecisoraColegiadaList(){
    	if (unidadeDecisoraColegiadaList == null){
    		unidadeDecisoraColegiadaList = unidadeDecisoraColegiadaManager.getUnidadeDecisoraListByIdUsuario((Integer) getInstanceId()); 
    	}
    	return unidadeDecisoraColegiadaList;
    }
    
    @Override
    public void setInstance(UsuarioLogin instance) {
        super.setInstance(instance);
        this.podeEditarLogin = podeEditarLogin();
    }
    
    private boolean podeEditarLogin() {
        return getInstance() == null 
                || getInstance().getLogin() == null 
                || !getManager().existeTaskInstaceComUsuario(getInstance().getLogin());
    }
    
    public boolean isPodeEditarLogin() {
        return podeEditarLogin;
    }

    @Override
    public String save() {
        try {
            return super.save();
        } catch (BusinessException e) {
            LOG.error("", e);
            FacesMessages.instance().add(e.getMessage());
        }
        return null;
    }

    @Override
    protected UsuarioLoginManager getManager() {
        return usuarioLoginManager;
    }
}
