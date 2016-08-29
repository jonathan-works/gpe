package br.com.infox.epp.system.custom.variables;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;

@ViewScoped
@Named
public class CustomVariableDataList extends DataList<CustomVariable> {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_JPQL = "select o from CustomVariable o ";
	private static final String DEFAULT_ORDER = "o.codigo";
	
	private String codigo;
	private TipoCustomVariableEnum tipo;
	
	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_JPQL;
	}
	
	@Override
	protected void addRestrictionFields() {
		addRestrictionField("codigo", "o.codigo", RestrictionType.contendoLower);
		addRestrictionField("tipo", "o.tipo", RestrictionType.igual);
	}
	
	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		HashMap<String, String> order = new HashMap<>();
		order.put("codigoCustomVariable", "o.codigo");
		order.put("tipoCustomVariableSearch", "o.tipo");
		return order;
	}

	public TipoCustomVariableEnum[] getTiposDisponiveis() {
		return TipoCustomVariableEnum.values();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public TipoCustomVariableEnum getTipo() {
		return tipo;
	}

	public void setTipo(TipoCustomVariableEnum tipo) {
		this.tipo = tipo;
	}
	
}
