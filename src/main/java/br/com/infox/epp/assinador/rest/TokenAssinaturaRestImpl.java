package br.com.infox.epp.assinador.rest;

import javax.inject.Inject;

import br.com.infox.assinador.rest.api.TokenAssinaturaBaseResource;

public class TokenAssinaturaRestImpl implements TokenAssinaturaRest {

    @Inject
    private TokenAssinaturaBaseResourcetImpl tokenAssinaturaBaseResource;

    public TokenAssinaturaBaseResource getBaseResource() {
        return tokenAssinaturaBaseResource;
    }

}
