package br.com.infox.epp.redistribuicao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.PersistenceController;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TipoRedistribuicaoService extends PersistenceController {

	@Inject
	private TipoRedistribuicaoSearch tipoRedistribuicaoSearch;
	
    public void inactivate(TipoRedistribuicao tipoRedistribuicao) {
    	tipoRedistribuicao.setAtivo(false);
    	getEntityManager().merge(tipoRedistribuicao);
    	getEntityManager().flush();
    }
    
    public boolean existeTipoRedistribuicao(String codigo, Long idIgnorado) {
    	return tipoRedistribuicaoSearch.existeTipoRedistribuicao(codigo, idIgnorado);
    }
    
    public void remove(TipoRedistribuicao td) {
        getEntityManager().remove(td);
        getEntityManager().flush();
    }

    public void persist(TipoRedistribuicao td) {
        getEntityManager().persist(td);
        getEntityManager().flush();
    }
    
    public List<TipoRedistribuicao> listAtivos() {
    	return tipoRedistribuicaoSearch.listAtivos();
    }
    
}
