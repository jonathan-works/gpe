package br.com.infox.epp.access.crud;

import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;

@Name(UsuarioLoginCrudAction.NAME)
public class UsuarioLoginCrudAction extends AbstractCrudAction<UsuarioLogin, UsuarioLoginManager> {
    
	private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginCrudAction";
    
    @In
    private UnidadeDecisoraMonocraticaManager unidadeDecisoraMonocraticaManager;
    @In
    private UnidadeDecisoraColegiadaManager unidadeDecisoraColegiadaManager;
    
    private List<Map<String, String>> unidadeDecisoraMonocraticaList;
    private List<UnidadeDecisoraColegiada> unidadeDecisoraColegiadaList;
    
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
    
    public List<UnidadeDecisoraColegiada> getUnidadeDecisoraColegiadaList(){
    	if (unidadeDecisoraColegiadaList == null){
    		unidadeDecisoraColegiadaList = unidadeDecisoraColegiadaManager.getUnidadeDecisoraListByIdUsuario((Integer) getInstanceId()); 
    	}
    	return unidadeDecisoraColegiadaList;
    }

}
