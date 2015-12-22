package br.com.infox.epp.processo.dao;

import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.RELACIONAMENTO_BY_PROCESSO;

import java.util.HashMap;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoExterno;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;

@AutoCreate
@Name(RelacionamentoDAO.NAME)
@Stateless
public class RelacionamentoDAO extends DAO<Relacionamento> {

	public static final String NAME = "relacionamentoDAO";
	private static final long serialVersionUID = 1L;

	public Relacionamento getRelacionamentoByProcesso(Processo processo) {
		final HashMap<String, Object> parameters = new HashMap<>();
		parameters.put(ID_PROCESSO, processo);
		return getNamedSingleResult(RELACIONAMENTO_BY_PROCESSO, parameters);
	}

    protected Relacionamento getRelacionamento(RelacionamentoProcessoInterno rel1, RelacionamentoProcesso rel2) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("processo1", rel1.getProcesso());
        
        String query = "select r from RelacionamentoProcessoInterno rp inner join rp.relacionamento r, "
        		+ rel2.getClass().getSimpleName() + " rp2 inner join rp2.relacionamento r2 "
        		+ "where r.idRelacionamento = r2.idRelacionamento "        		
        		+ "and rp.processo=:processo1 ";
        		if(rel2 instanceof RelacionamentoProcessoInterno) {
        			query += "and rp2.processo=:processo2 ";
        	        parameters.put("processo2", ((RelacionamentoProcessoInterno) rel2).getProcesso());
        		}
        		else if(rel2 instanceof RelacionamentoProcessoExterno)
        		{
        			query += "and rp2.numeroProcesso=:processo2 ";
        	        parameters.put("processo2", ((RelacionamentoProcessoExterno) rel2).getNumeroProcesso());        			
        		}
        		else
        		{
        			throw new UnsupportedOperationException();
        		}
        final Relacionamento retorno = getSingleResult(query, parameters);
        return retorno;
    }
    
	protected void remove(RelacionamentoProcessoInterno rp1, RelacionamentoProcesso rp2) {
		Relacionamento relacionamento = getRelacionamento(rp1, rp2);
		EntityManager em = getEntityManager();
		
		for(RelacionamentoProcesso rp : relacionamento.getRelacionamentosProcessos()) {
			em.remove(rp);
		}
		em.remove(relacionamento);
		em.flush();
	}

	public void remove(Processo processo, Processo processoInternoRelacionado) {
		RelacionamentoProcessoInterno rp1 = new RelacionamentoProcessoInterno(null, processo);
		RelacionamentoProcessoInterno rp2 = new RelacionamentoProcessoInterno(null, processoInternoRelacionado);
		remove(rp1, rp2);
	}

	public void remove(Processo processo, String numeroProcessoExterno) {
		RelacionamentoProcessoInterno rp1 = new RelacionamentoProcessoInterno(null, processo);
		RelacionamentoProcessoExterno rp2 = new RelacionamentoProcessoExterno(null, numeroProcessoExterno);
		remove(rp1, rp2);
	}
}
