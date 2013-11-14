package br.com.infox.epp.endereco.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Cep;
import br.com.infox.ibpm.entity.Estado;

@Name(CepList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CepList extends EntityList<Cep> {
	
	public static final String NAME = "cepList";

	private static final long serialVersionUID = 1L;
	private Estado estado;
	
	private static final String DEFAULT_EJBQL = "select o from Cep o";
	private static final String DEFAULT_ORDER = "numeroCep";
	private static final String R1 = "o.municipio.estado = #{cepList.estado}";
	 
	protected void addSearchFields() {
		addSearchField("numeroCep", SearchCriteria.CONTENDO);
		addSearchField("nomeLogradouro", SearchCriteria.CONTENDO);
		addSearchField("nomeBairro", SearchCriteria.CONTENDO);
		addSearchField("municipio", SearchCriteria.IGUAL);
		addSearchField("municipio.estado", SearchCriteria.IGUAL, R1);
		addSearchField("ativo", SearchCriteria.IGUAL);
	}

	protected Map<String, String> getCustomColumnsOrder() {
		Map<String, String> order = new HashMap<String, String>();
		order.put("municipio", "municipio.municipio");
		order.put("municipio.estado", "municipio.estado.estado");
		return order;
	}

	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Estado getEstado() {
		return estado;
	}
    @Override
    public void newInstance() {
        estado = null;
        super.newInstance();
    }
	public static CepList instance() {
		return (CepList) Component.getInstance(NAME);
	}
	
}