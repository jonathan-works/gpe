package br.com.infox.ibpm.variable;

import org.jbpm.context.def.VariableAccess;

import com.google.gson.Gson;

import br.com.infox.core.util.StringUtil;

public class VariableFileHandler {

	private VariableAccess variableAccess;
	private String pasta;

	public void init(VariableAccess variableAccess) {
		this.variableAccess = variableAccess;
		if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
			FileConfig config = fromJson(this.variableAccess.getConfiguration());
			setPasta(config.getPasta());
		}
	}

	public String getPasta() {
		return pasta;
	}

	public static FileConfig fromJson(String configuration) {
		return new Gson().fromJson(configuration, FileConfig.class);
	}

	public static String toJson(FileConfig configuration) {
		return new Gson().toJson(configuration, FileConfig.class);
	}

	public void setPasta(String pasta) {
		this.pasta = pasta;
		if (this.pasta != null) {
			FileConfig config;
			if (this.variableAccess.getConfiguration() != null)
				config = fromJson(this.variableAccess.getConfiguration());
			else
				config = new FileConfig();
			config.setPasta(pasta);
			this.variableAccess.setConfiguration(new Gson().toJson(config, FileConfig.class));
		}
	}

	public static class FileConfig {
		private String pasta;

		public String getPasta() {
			return pasta;
		}

		public void setPasta(String pasta) {
			this.pasta = pasta;
		}
	}

}
