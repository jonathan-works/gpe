package br.com.infox.epp.certificadoeletronico.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.certificadoeletronico.CertificadoEletronicoSearch;
import br.com.infox.epp.certificadoeletronico.CertificadoEletronicoService;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronico;
import br.com.infox.seam.exception.BusinessException;
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
    @Getter
    private List<CertificadoEletronicoVO> certificadosGerados;
    @Inject
    private CertificadoEletronicoService certificadoEletronicoService;
    @Inject
    private CertificadoEletronicoSearch certificadoEletronicoSearch;

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
            this.certificadosGerados = certificadoEletronicoSearch.getListaCertificadoEletronicoVO();
        } else {
            this.exibeBotaoGerarCertificado = true;
        }
    }

    @ExceptionHandled(successMessage = "Certificado gerado com sucesso")
    public void gerarCertificadoRaiz() {
        certificadoEletronicoService.gerarCertificadoRaiz();
        refreshInitValues();
    }

    @ExceptionHandled(successMessage = "Certificado reemitido com sucesso")
    public void reemitirCertificado(CertificadoEletronicoVO certificadoEletronicoVO) {
        if(certificadoEletronicoVO != null) {
            certificadoEletronicoService.reemitirCertificadoEletronico(certificadoEletronicoVO.getIdPessoa());
            refreshInitValues();
        } else {
            throw new BusinessException("Houve um erro ao tentar reemitir");
        }
    }

}