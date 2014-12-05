package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.EppProperties;

@Name(ModeloComunicacaoRascunhoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class ModeloComunicacaoRascunhoList extends EntityList<ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoRascunhoList";
	
	private static final String DEFAULT_EJBQL = "select m.* from tb_modelo_comunicacao m where "
			+ "m.id_processo = #{modeloComunicacaoRascunhoList.processo.idProcesso} and "
			+ "not exists (select 1 from jbpm_variableinstance v where v.name_ = 'idModeloComunicacao' and "
				+ "v.longvalue_ = m.id_modelo_comunicacao)";
	private static final String DEFAULT_ORDER = "m.id_modelo_comunicacao";

	private Processo processo;
	
	public ModeloComunicacaoRascunhoList() {
		setNativeQuery(true);
		setResultClass(ModeloComunicacao.class);
	}
	
	@Override
	protected void addSearchFields() {
	}

	@Override
	protected String getDefaultEjbql() {
		String banco = EppProperties.getInstance().getProperty(EppProperties.PROPERTY_TIPO_BANCO_DADOS);
		String fragment = "";
		if ("postgresql".equalsIgnoreCase(banco)) {
			fragment = " and (m.in_finalizada = false or m.id_localizacao_resp_assinat is null) ";
		} else if ("sqlserver".equalsIgnoreCase(banco)) {
			fragment = " and (m.in_finalizada = 0 or m.id_localizacao_resp_assinat is null) ";
		}
		return DEFAULT_EJBQL + fragment;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	public Processo getProcesso() {
		return processo;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
}
