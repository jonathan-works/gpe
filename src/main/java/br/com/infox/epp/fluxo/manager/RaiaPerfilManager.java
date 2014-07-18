package br.com.infox.epp.fluxo.manager;

import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.PerfilDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Perfil;
import br.com.infox.epp.fluxo.dao.RaiaPerfilDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.RaiaPerfil;

@Name(RaiaPerfilManager.NAME)
@AutoCreate
public class RaiaPerfilManager extends Manager<RaiaPerfilDAO, RaiaPerfil> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "raiaPerfilManager";

    @In private PerfilDAO perfilDAO;
    
    public List<RaiaPerfil> listByPerfil(Perfil perfil) {
        return getDao().listByPerfil(perfil);
    }
    
    public List<RaiaPerfil> listByLocalizacao(Localizacao localizacao) {
        return getDao().listByLocalizacao(localizacao);
    }
    
    public void removerRaiaPerfisDoFluxo(Fluxo fluxo) throws DAOException {
        getDao().removerRaiaPerfisDoFluxo(fluxo);
    }
    
    private void criarRaiaPerfis(Fluxo fluxo, Map<String, Swimlane> swimlanes) throws DAOException {
        for (Swimlane swimlane : swimlanes.values()) {
            if (swimlane.getPooledActorsExpression() == null || swimlane.getPooledActorsExpression().isEmpty()) {
                continue;
            }
            String[] perfis = swimlane.getPooledActorsExpression().split(",");
            for (String perfil : perfis) {
                RaiaPerfil raiaPerfil = new RaiaPerfil();
                raiaPerfil.setFluxo(fluxo);
                raiaPerfil.setNomeRaia(swimlane.getName());
                raiaPerfil.setPerfil(perfilDAO.getReference(Integer.valueOf(perfil)));
                persist(raiaPerfil);
            }
        }
    }
    
    public void atualizarRaias(Fluxo fluxo, Map<String, Swimlane> swimlanes) throws DAOException {
        removerRaiaPerfisDoFluxo(fluxo);
        criarRaiaPerfis(fluxo, swimlanes);
    }
    
    public boolean existeRaiaPerfilComHierarquiaLocalizacao(Localizacao localizacao) {
        return getDao().existeRaiaPerfilComHierarquiaLocalizacao(localizacao);
    }
}
