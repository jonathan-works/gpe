package br.com.infox.ibpm.variable;

import org.jbpm.context.def.VariableAccess;

import com.google.gson.Gson;

import br.com.infox.core.util.StringUtil;


public class VariableStringHandler {

	private VariableAccess variableAccess;
	private String mascara = "";

	public void init(VariableAccess variableAccess) {
		this.variableAccess = variableAccess;
		if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
			StringConfig config = fromJson(this.variableAccess.getConfiguration());
			setMascara(config.getMascara());
		}
	}

	public static StringConfig fromJson(String configuration) {
		return new Gson().fromJson(configuration, StringConfig.class);
	}

	public static String toJson(StringConfig configuration) {
		return new Gson().toJson(configuration, StringConfig.class);
	}

	public String getMascara() {
		return mascara;
	}

	public void setMascara(String mascara) {
		this.mascara = mascara;
    	if (this.mascara != null) {
    		StringConfig config;
    		if(this.variableAccess.getConfiguration() != null)
    			config = fromJson(this.variableAccess.getConfiguration());
    		else
    			config = new StringConfig();
            config.setMascara(mascara);
            this.variableAccess.setConfiguration(new Gson().toJson(config, StringConfig.class));
        }
	}

	public static class StringConfig {

		private String mascara;

		public String getMascara() {
			return mascara;
		}

		public void setMascara(String mascara) {
			this.mascara = mascara;
		}

		
	}
}
