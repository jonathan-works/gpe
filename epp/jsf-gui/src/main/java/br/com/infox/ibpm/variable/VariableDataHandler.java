package br.com.infox.ibpm.variable;

import org.jbpm.context.def.VariableAccess;

import com.google.gson.Gson;

import br.com.infox.core.util.StringUtil;
import br.com.infox.ibpm.variable.type.ValidacaoDataEnum;

public class VariableDataHandler {

	private VariableAccess variableAccess;
	private ValidacaoDataEnum validacaoDataEnum;
	
	public void init(VariableAccess variableAccess) {
		this.variableAccess = variableAccess;
		if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
			setValidacaoDataEnum(fromJson(this.variableAccess.getConfiguration()).getTipoValidacaoData());
		}
	}
	
    public ValidacaoDataEnum getValidacaoDataEnum() {
        return validacaoDataEnum;
    }

    public void setValidacaoDataEnum(ValidacaoDataEnum validacaoDataEnum) {
    	this.validacaoDataEnum = validacaoDataEnum;
    	if (this.validacaoDataEnum != null) {
            DataConfig config = new DataConfig();
            config.setTipoValidacaoData(validacaoDataEnum);
            this.variableAccess.setConfiguration(new Gson().toJson(config, DataConfig.class));
        } else {
        	this.variableAccess.setConfiguration(null);
        }
    }
    
    public ValidacaoDataEnum[] getTypeDateValues() {
        return ValidacaoDataEnum.values();
    }
	
	public static DataConfig fromJson(String configuration) {
		return new Gson().fromJson(configuration, DataConfig.class);
	}
	
	public static String toJson(DataConfig configuration) {
		return new Gson().toJson(configuration, DataConfig.class);
	}
	
	public static class DataConfig {
		private ValidacaoDataEnum tipoValidacaoData;

		public ValidacaoDataEnum getTipoValidacaoData() {
			return tipoValidacaoData;
		}

		public void setTipoValidacaoData(ValidacaoDataEnum tipoValidacaoData) {
			this.tipoValidacaoData = tipoValidacaoData;
		}
	}
}
