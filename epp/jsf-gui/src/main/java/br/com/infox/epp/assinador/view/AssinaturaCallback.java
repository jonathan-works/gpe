package br.com.infox.epp.assinador.view;

import java.util.List;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.epp.assinador.DadosAssinatura;

public interface AssinaturaCallback {
    void onSuccess(List<DadosAssinatura> dadosAssinatura);
    void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinatura);
}
