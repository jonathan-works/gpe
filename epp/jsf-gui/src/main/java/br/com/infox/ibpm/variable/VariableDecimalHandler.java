package br.com.infox.ibpm.variable;

import org.jbpm.context.def.VariableAccess;

import com.google.gson.Gson;

import br.com.infox.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

public class VariableDecimalHandler {

    private VariableAccess variableAccess;
    private Integer casasDecimais;

    public void init(VariableAccess variableAccess) {
        this.variableAccess = variableAccess;
        if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
            DecimalConfig decimalConfig = fromJson(this.variableAccess.getConfiguration());
            setCasasDecimais(decimalConfig.casasDecimais);
        }
    }

    public static DecimalConfig fromJson(String configuration) {
        return new Gson().fromJson(configuration, DecimalConfig.class);
    }

    public static String toJson(DecimalConfig configuration) {
        return new Gson().toJson(configuration, DecimalConfig.class);
    }

    public Integer getCasasDecimais() {
        return casasDecimais;
    }

    public void setCasasDecimais(Integer casas) {
        casasDecimais = casas;
        DecimalConfig config;
        if (this.variableAccess.getConfiguration() != null)
            config = fromJson(this.variableAccess.getConfiguration());
        else
            config = new DecimalConfig();
        config.setCasasDecimais(casasDecimais);
        this.variableAccess.setConfiguration(new Gson().toJson(config, DecimalConfig.class));
    }

    public static class DecimalConfig {

        @Getter @Setter
        private Integer casasDecimais;

    }
}
