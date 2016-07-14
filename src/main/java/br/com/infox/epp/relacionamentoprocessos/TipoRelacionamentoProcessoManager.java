package br.com.infox.epp.relacionamentoprocessos;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;

@AutoCreate
@Name(TipoRelacionamentoProcessoManager.NAME)
@Stateless
public class TipoRelacionamentoProcessoManager extends Manager<TipoRelacionamentoProcessoDAO, TipoRelacionamentoProcesso> {

    public static final String NAME = "tipoRelacionamentoProcessoManager";
    private static final long serialVersionUID = 1L;
    
    @Inject
    private TipoRelacionamentoProcessoDAO tipoRelacionamentoProcessoDAO;
    
    public EntityManager getEntityManager() {
    	return EntityManagerProducer.getEntityManager();
    }
    
    public TipoRelacionamentoProcesso findByCodigo(String codigo) {
    	return tipoRelacionamentoProcessoDAO.findByCodigo(codigo);
    }

}
