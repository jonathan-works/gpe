package br.com.infox.ibpm.event;

import java.util.List;

public interface BpmExpressionService {
    String NAME = "bpmExpressionService";

    List<ExternalMethod> getExternalMethods();

}
