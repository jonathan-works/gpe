package br.com.infox.epp.movimentarlote;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.documento.query.DocumentosParaSeremAssinadosQuery;
import br.com.infox.epp.documento.query.TarefasMovimentarLoteQuery;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static br.com.infox.epp.documento.query.TarefasMovimentarLoteQuery.PARAM_TASK_INSTANCE;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TaskInstancePermitidaMovimentarLoteSearch extends PersistenceController {


    public List<String> getTaskIntancesAptasAMovimentarEmLote(Set<String> idTaskInstance){

		List<BigDecimal> resultList = getEntityManager().createNativeQuery(TarefasMovimentarLoteQuery.PODE_MOVIMENTAR_EM_LOTE_QUERY
				.toString()).setParameter(PARAM_TASK_INSTANCE, idTaskInstance).getResultList();


		List<String> listaTaskInstance = new ArrayList<>();
		for(BigDecimal  record : resultList){
			listaTaskInstance.add(record.toString());
		}

		return listaTaskInstance;
    }
}
