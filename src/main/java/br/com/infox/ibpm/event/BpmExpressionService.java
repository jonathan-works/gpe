package br.com.infox.ibpm.event;

import java.util.List;

public abstract class BpmExpressionService {
    public final static String NAME = "bpmExpressionService";

    public abstract List<ExternalMethod> getExternalMethods();
    public abstract List<ExternalMethod> getExternalRaiaDinamicaMethods();

}
