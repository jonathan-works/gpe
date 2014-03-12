package br.com.infox.epp.processo.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.dao.RelacionamentoProcessoDAO;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;

@AutoCreate
@Name(RelacionamentoProcessoManager.NAME)
public class RelacionamentoProcessoManager extends Manager<RelacionamentoProcessoDAO, RelacionamentoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "relacionamentoProcessoManager";

    @In
    private RelacionamentoManager relacionamentoManager;

    @Override
    public RelacionamentoProcesso persist(final RelacionamentoProcesso instance) throws DAOException {
        if (instance.getAtivo() == null) {
            instance.setAtivo(Boolean.TRUE);
        }
        if (instance.getRelacionamento() == null) {
            Relacionamento relacionamento = relacionamentoManager.getRelacionamentoByProcesso(instance.getProcesso());
            if (relacionamento == null) {
                relacionamento = relacionamentoManager.persist(new Relacionamento());
            }
            instance.setRelacionamento(relacionamento);
        }
        instance.setDataRelacionamento(new Date());
        instance.setNomeUsuario(Authenticator.getUsuarioLogado().getNomeUsuario());
        final RelacionamentoProcessoDAO dao = getDao();
        dao.persist(instance);
        try {
            final Object id = EntityUtil.getId(instance).getReadMethod().invoke(instance);
            return dao.find(id);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new DAOException(e);
        }
    }

}
