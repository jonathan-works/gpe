package br.com.infox.epp.rest;

import br.com.infox.epp.ws.exception.ErroServico;

public class RestException implements ErroServico {
    
    private String code;
    private String message;
    
    public RestException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
