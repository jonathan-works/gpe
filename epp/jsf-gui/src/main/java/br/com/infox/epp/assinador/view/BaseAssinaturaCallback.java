package br.com.infox.epp.assinador.view;

import java.util.List;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.epp.assinador.DadosAssinatura;

public class BaseAssinaturaCallback implements AssinaturaCallback {

    @Override
    public void onSuccess(List<DadosAssinatura> dadosAssinatura) {
    }

    @Override
    public void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinatura) {
    }

}
