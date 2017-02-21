package br.com.infox.epp.fluxo.definicaosinais;

import java.util.ArrayList;
import java.util.List;

public class DefinicaoSinais implements SignalDefinitionSource {
    public static final SignalDefinition REDISTRIBUICAO = new SignalDefinition("redistribuicao");

    @Override
    public List<SignalDefinition> getSignalDefinitions() {
        List<SignalDefinition> retorno = new ArrayList<>();
        retorno.add(REDISTRIBUICAO);
        return retorno;
    }

}
