package br.com.infox.epp.loglab.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.loglab.search.ServidorSearch;
import br.com.infox.epp.loglab.service.ServidorService;
import br.com.infox.epp.loglab.vo.ServidorVO;
import br.com.infox.jsf.util.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ServidorView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ServidorService servidorService;
    @Inject
    private ServidorSearch servidorSearch;

    @Getter
    @Setter
    private ServidorVO servidorVO;
    @Getter
    @Setter
    private String numeroCpf;
    @Getter
    @Setter
    private List<ServidorVO> servidorList;

    @PostConstruct
    protected void init() {
    	limpar();
	}

    public void consultarTurmalina() {
    	if (numeroCpf != null) {
    		servidorList = servidorSearch.getDadosServidor(numeroCpf);
            JsfUtil.instance().execute("PF('listaServidoresDialog').show();");
    	}
    }

    public void novo() {
    	limpar();
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void gravar() {
    	servidorService.gravar(servidorVO);
    }

    @ExceptionHandled(MethodType.UPDATE)
    public void atualizar() {
    	servidorService.gravar(servidorVO);
    }

    public void limpar() {
    	servidorVO = null;
    	numeroCpf = null;
    	servidorList = null;
    }
}
