package br.com.infox.ibpm.variable;

import org.jbpm.context.def.VariableAccess;

import com.google.gson.Gson;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaSearch;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

public class VariableDominioEnumerationHandler {

	private VariableAccess variableAccess;
	private DominioVariavelTarefa dominioVariavelTarefa;
	
	public void init(VariableAccess variableAccess) {
		this.variableAccess = variableAccess;
		if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
			DominioVariavelTarefaSearch dominioVariavelTarefaSearch = BeanManager.INSTANCE.getReference(DominioVariavelTarefaSearch.class);
            this.dominioVariavelTarefa = dominioVariavelTarefaSearch.findByCodigo(fromJson(this.variableAccess.getConfiguration()).getCodigoDominio());
		}
	}
	
    public DominioVariavelTarefa getDominioVariavelTarefa() {
        return dominioVariavelTarefa;
    }

    public void setDominioVariavelTarefa(DominioVariavelTarefa dominioVariavelTarefa) {
        this.dominioVariavelTarefa = dominioVariavelTarefa;
        if (this.dominioVariavelTarefa != null ) {
            EnumerationConfig config = new EnumerationConfig();
            config.setCodigoDominio(this.dominioVariavelTarefa.getCodigo());
            this.variableAccess.setConfiguration(new Gson().toJson(config, EnumerationConfig.class));
        } else {
        	this.variableAccess.setConfiguration(null);
        }
    }

	public static EnumerationConfig fromJson(String configuration) {
		return new Gson().fromJson(configuration, EnumerationConfig.class);
	}

	public static String toJson(EnumerationConfig configuration) {
		return new Gson().toJson(configuration, EnumerationConfig.class);
	}

	public static class EnumerationConfig {
		private String codigoDominio;

		public String getCodigoDominio() {
			return codigoDominio;
		}

		public void setCodigoDominio(String codigoDominio) {
			this.codigoDominio = codigoDominio;
		}
	}
}
