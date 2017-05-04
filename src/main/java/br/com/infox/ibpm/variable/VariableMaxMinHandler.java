package br.com.infox.ibpm.variable;

import org.jbpm.context.def.VariableAccess;

import com.google.gson.Gson;

import br.com.infox.core.util.StringUtil;

public class VariableMaxMinHandler {

	private VariableAccess variableAccess;
	private Double maximo = 0D;
	private Double minimo = 0D;

	public void init(VariableAccess variableAccess) {
		this.variableAccess = variableAccess;
		if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
			MaxMinConfig maxMin = fromJson(this.variableAccess.getConfiguration());
			setMaximo(maxMin.maximo);
			setMinimo(maxMin.minimo);
		}
	}

	public static MaxMinConfig fromJson(String configuration) {
		return new Gson().fromJson(configuration, MaxMinConfig.class);
	}

	public static String toJson(MaxMinConfig configuration) {
		return new Gson().toJson(configuration, MaxMinConfig.class);
	}

	public Double getMaximo() {
		return maximo;
	}

	public void setMaximo(Double max) {
		/*if (max == null) {
			max = 0D;
		}*/
		maximo = max;
		MaxMinConfig config;
		if (this.variableAccess.getConfiguration() != null)
			config = fromJson(this.variableAccess.getConfiguration());
		else
			config = new MaxMinConfig();
		config.setMaximo(maximo);
		this.variableAccess.setConfiguration(new Gson().toJson(config, MaxMinConfig.class));
	}

	public Double getMinimo() {
		return minimo;
	}

	public void setMinimo(Double min) {
		/*if (min == null) {
			min = 0D;
		}*/
		minimo = min;
		MaxMinConfig config;
		if (this.variableAccess.getConfiguration() != null)
			config = fromJson(this.variableAccess.getConfiguration());
		else
			config = new MaxMinConfig();
		config.setMinimo(minimo);
		this.variableAccess.setConfiguration(new Gson().toJson(config, MaxMinConfig.class));
	}

	public static class MaxMinConfig {

		private Double maximo;
		private Double minimo;

		public Double getMaximo() {
			return maximo;
		}

		public void setMaximo(Double maximo) {
			this.maximo = maximo;
		}

		public Double getMinimo() {
			return minimo;
		}

		public void setMinimo(Double minimo) {
			this.minimo = minimo;
		}
	}
}
