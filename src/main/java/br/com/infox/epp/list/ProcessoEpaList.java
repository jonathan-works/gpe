package br.com.infox.epp.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.util.constants.WarningConstants;
import br.com.infox.access.entity.UsuarioLogin;

@Name(ProcessoEpaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class ProcessoEpaList extends EntityList<ProcessoEpa> {
	public static final String NAME = "processoEpaList";
	
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_EJBQL = "select o from ProcessoEpa o";
	private static final String DEFAULT_ORDER = "dataInicio DESC";
	private static final String R1 = "cast(dataInicio as date) >= #{processoEpaList.entity.dataInicio}";
	private static final String R2 = "cast(dataFim as date)<= #{processoEpaList.entity.dataFim}";
	
	private List<UsuarioLogin> listaUsuarios;
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	private void iniciaListaUsuarios()	{
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct user from Processo o ");
		sb.append("join o.usuarioCadastroProcesso user");
		listaUsuarios = getEntityManager().createQuery(sb.toString()).getResultList();
	}
	
	@Override
	protected void addSearchFields() {
		addSearchField("numeroProcesso", SearchCriteria.IGUAL);
		addSearchField("usuarioCadastroProcesso", SearchCriteria.IGUAL);
		addSearchField("dataInicio", SearchCriteria.MAIOR_IGUAL, R1);
		addSearchField("dataFim", SearchCriteria.MENOR_IGUAL, R2);
		iniciaListaUsuarios();
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

	public List<UsuarioLogin> getListaUsuarios() {
		return listaUsuarios;
	}
	public void setListaUsuarios(List<UsuarioLogin> listaUsuarios)	{
		this.listaUsuarios = listaUsuarios;
	}

}
