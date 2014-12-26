package br.com.infox.epp.access.crud;

import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;
import br.com.infox.seam.exception.BusinessException;

@Name(UsuarioLoginCrudAction.NAME)
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin, UsuarioLoginManager> {
    
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioLoginCrudAction.class);
	private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginCrudAction";
    
    @In
    private UnidadeDecisoraMonocraticaManager unidadeDecisoraMonocraticaManager;
    @In
    private UnidadeDecisoraColegiadaManager unidadeDecisoraColegiadaManager;
    
    private List<Map<String, String>> unidadeDecisoraMonocraticaList;
    private List<Map<String, String>> unidadeDecisoraColegiadaList;
    
    public void onClickVinculoUnidadeDecisoraTab(){
    	unidadeDecisoraMonocraticaList = null;
    	unidadeDecisoraColegiadaList = null;
    }

    @Override
    public void newInstance() {
        super.newInstance();
        final UsuarioLogin usuarioLogin = getInstance();
        usuarioLogin.setBloqueio(Boolean.FALSE);
        usuarioLogin.setProvisorio(Boolean.FALSE);
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
    public String save() {
        try {
            return super.save();
        } catch (BusinessException e) {
            LOG.error("", e);
            FacesMessages.instance().add(e.getMessage());
        }
        return null;
    }
}
