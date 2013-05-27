package br.com.infox.list;

import java.util.Map;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;

public class UsuarioLocalizacaoList extends EntityList<UsuarioLocalizacao> {
	
	public static final String NAME = "usuarioLocalizacaoList";
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_EJBQL = "select o from UsuarioLocalizacao o";
	public static final String DEFAULT_ORDER = "o.papel";

	@Override
	protected void addSearchFields() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getDefaultEjbql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDefaultOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		// TODO Auto-generated method stub
		return null;
	}

}
