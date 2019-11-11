package br.com.infox.epp.system.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.system.entity.EntityLog;

@Stateless
@AutoCreate
@Name(EntidadeLogDAO.NAME)
public class EntidadeLogDAO extends DAO<EntityLog> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "entidadeLogDAO";

    @SuppressWarnings(UNCHECKED)
    public List<UsuarioLogin> getUsuariosQuePossuemRegistrosDeLog() {
        String hql = "select o from UsuarioLogin o "
                + "where o.entityLogList.size > 0 "
                + "order by o.idUsuarioLogin";
        return (List<UsuarioLogin>) getEntityManager().createQuery(hql).getResultList();
    }

    @SuppressWarnings(UNCHECKED)
    public List<String> getEntidadesQuePodemPossuirLog() {
        String hql = "select distinct o.nomeEntidade as entidade from EntityLog o order by o.nomeEntidade";
        return (List<String>) getEntityManager().createQuery(hql).getResultList();
    }

}
