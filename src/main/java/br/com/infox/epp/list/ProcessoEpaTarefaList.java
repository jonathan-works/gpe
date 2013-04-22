package br.com.infox.epp.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.ProcessoEpaTarefa;
import br.com.itx.util.StringUtil;


/**
 * EntityList que consulta as tarefas de um determinado processo
 * @author tassio
 *
 */
@Name(ProcessoEpaTarefaList.NAME)
public class ProcessoEpaTarefaList extends EntityList<ProcessoEpaTarefa> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoEpaTarefaList";
	
	private static final String DEFAULT_EJBQL = "select o from ProcessoEpaTarefa o";
	private static final String DEFAULT_ORDER = "o.idProcessoTarefa";

	@Override
	protected void addSearchFields() {
		addSearchField("processoEpa", SearchCriteria.igual);
	}
	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}
	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}
	
	public String rowClasses() {
		List<Object> classes = new ArrayList<Object>();
		for (ProcessoEpaTarefa row: list(15)) {
			if (row.getPorcentagem() != null && row.getPorcentagem() > 100) {
				classes.add("red-back");
			} else {
				classes.add("white-back");
			}
		}
		return StringUtil.concatList(classes, ",");
	}
	
}
