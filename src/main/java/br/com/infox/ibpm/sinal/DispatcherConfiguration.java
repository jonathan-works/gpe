package br.com.infox.ibpm.sinal;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import br.com.infox.core.util.StringUtil;

public class DispatcherConfiguration {

    private String codigoSinal;
    private List<SignalParam> signalParams;
    
    public DispatcherConfiguration() {
    }

    public DispatcherConfiguration(String codigoSinal) {
        this.codigoSinal = codigoSinal;
    }

    public String getCodigoSinal() {
        return codigoSinal;
    }

    public void addSignalParam(SignalParam signalVariable) {
        if (signalParams == null) signalParams = new ArrayList<>();
        signalParams.add(signalVariable);
    }
    
    public static DispatcherConfiguration fromJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            return new Gson().fromJson(json, DispatcherConfiguration.class);
        } else {
            return new DispatcherConfiguration();
        }
    }
    
    public String toJson() {
        return new Gson().toJson(this);
    }
   
}
