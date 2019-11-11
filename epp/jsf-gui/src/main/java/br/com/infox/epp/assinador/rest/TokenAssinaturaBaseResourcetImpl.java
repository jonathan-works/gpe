package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import br.com.infox.assinador.rest.api.ExecutionConfiguration;
import br.com.infox.assinador.rest.api.TokenAssinaturaBaseResource;
import br.com.infox.assinador.rest.api.TokenAssinaturaResource;

public class TokenAssinaturaBaseResourcetImpl implements TokenAssinaturaBaseResource {

    @Inject private TokenAssinaturaResourceImpl tokenAssinaturaResourceImpl;

    @Override
    public TokenAssinaturaResource getTokenAssinaturaResource(String token) {
        tokenAssinaturaResourceImpl.setToken(token);
        return tokenAssinaturaResourceImpl;
    }

    @Override
    public ExecutionConfiguration getExecutionConfiguration() {
        ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
        executionConfiguration.setDebug(false);
        return executionConfiguration;
    }

}
