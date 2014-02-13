package br.com.infox.epp.processo.manager;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.dao.RelacionamentoProcessoDAO;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;

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
        Relacionamento relacionamento = relacionamentoManager.getRelacionamentoByProcesso(instance.getProcesso());
        if (relacionamento == null) {
            relacionamento = relacionamentoManager.persist(new Relacionamento());
        }
        instance.setDataRelacionamento(new Date());
        instance.setNomeUsuario(Authenticator.getUsuarioLogado().getNomeUsuario());
        return getDao().persist(instance);
    }
    
}
