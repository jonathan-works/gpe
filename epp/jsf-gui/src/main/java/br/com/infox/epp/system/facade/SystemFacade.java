package br.com.infox.epp.system.facade;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;

@Named
@ViewScoped
public class SystemFacade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ParametroManager parametroManager;

    private String exportarPDF;
    private String exportarXLS;

    @PostConstruct
    private void init() {
        exportarPDF = Boolean.FALSE.toString();
        Parametro parametro = parametroManager.getParametro("exportarPDF");
        if (parametro != null) {
            exportarPDF = parametro.getValorVariavel();
        }
        exportarXLS = Boolean.FALSE.toString();
        parametro = parametroManager.getParametro("exportarXLS");
        if (parametro != null) {
            exportarXLS = parametro.getValorVariavel();
        }
    }

    public String exportarPDF() {
        return exportarPDF;
    }

    public String exportarXLS() {
        return exportarXLS;
    }

}
