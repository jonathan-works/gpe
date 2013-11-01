package br.com.infox.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Parametro;

@Name(ParametroList.NAME)
@Scope(ScopeType.CONVERSATION)
public class ParametroList extends EntityList<Parametro> {
	
	public static final String NAME = "parametroList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Parametro o left " +
			"join o.usuarioModificacao u";
	
	private static final String DEFAULT_ORDER = "nomeVariavel asc";
	
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}
	
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	
	protected void addSearchFields() {
		addSearchField("nomeVariavel", SearchCriteria.INICIANDO);
		addSearchField("descricaoVariavel", SearchCriteria.CONTENDO);
		addSearchField("usuarioModificacao", SearchCriteria.IGUAL);
		addSearchField("ativo", SearchCriteria.IGUAL);
	}

	/**
	 * Coluna usuarioModificacao ordenada pelo nome do usuario.
	 * Caso seja a coluna usuarioModificacao nula, coloca os registros não nulos antes
	 */
	protected Map<String, String> getCustomColumnsOrder() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("usuarioModificacao", "case when (u.nome is null) then " +
				"#{parametroList.order.endsWith(' asc') ? 'ZZZ' : '  '} else u.nome end ");
		map.put("nomeVariavel", "o.nomeVariavel");
		map.put("descricaoVariavel", "o.descricaoVariavel");
		map.put("valorVariavel", "o.valorVariavel");
		map.put("dataAtualizacao", "o.dataAtualizacao");
		map.put("sistema", "o.sistema");
		map.put("ativo", "o.ativo");
		return map;
	}
	
}