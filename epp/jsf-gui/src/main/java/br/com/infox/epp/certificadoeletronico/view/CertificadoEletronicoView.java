package br.com.infox.epp.certificadoeletronico.view;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.certificadoeletronico.CertificadoEletronicoService;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronico;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class CertificadoEletronicoView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String tab = "principal";
    @Getter
    private Date dataInicio;
    @Getter
    private Date dataFim;
    @Getter
    private boolean exibeBotaoGerarCertificado;
    @Inject
    private CertificadoEletronicoService certificadoEletronicoService;

    @PostConstruct
    private void init() {
        refreshInitValues();
    }

    private void refreshInitValues() {
        CertificadoEletronico certificadoEletronicoRaiz = certificadoEletronicoService.getCertificadoEletronicoRaiz();
        if(certificadoEletronicoRaiz != null) {
            this.dataInicio = certificadoEletronicoRaiz.getDataInicio();
            this.dataFim = certificadoEletronicoRaiz.getDataFim();
            this.exibeBotaoGerarCertificado = false;
        } else {
            this.exibeBotaoGerarCertificado = true;
        }
    }

    @ExceptionHandled(successMessage = "Certificado gerado com sucesso")
    public void gerarCertificadoRaiz() {
        certificadoEletronicoService.gerarCertificadoRaiz();
        refreshInitValues();
    }

}