package br.com.infox.epp.ws.exception;

import javax.ws.rs.core.Response.Status;

import br.com.infox.epp.ws.services.MensagensErroService;

public class ConflictWSException extends RuntimeException implements ExcecaoServico {

    private static final long serialVersionUID = 1L;
    
    public ConflictWSException(String mensagem) {
        super(mensagem);
    }
    
    @Override
    public String getCode() {
        return MensagensErroService.CODIGO_DAO_EXCEPTION;
    }

    @Override
    public ErroServico getErro() {
        return this;
    }

    @Override
    public int getStatus() {
        return Status.CONFLICT.getStatusCode();
    }

}
