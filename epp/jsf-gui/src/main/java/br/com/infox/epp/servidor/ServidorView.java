package br.com.infox.epp.servidor;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ServidorView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private ServidorVO servidorVO;

    @PostConstruct
    protected void init() {
    	this.servidorVO = new ServidorVO();
	}

    public void consultarTurmalina() {
    }

    public void novo() {
        this.servidorVO = new ServidorVO();
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void gravar() {
    }

    @ExceptionHandled(MethodType.UPDATE)
    public void atualizar() {
    }

}
