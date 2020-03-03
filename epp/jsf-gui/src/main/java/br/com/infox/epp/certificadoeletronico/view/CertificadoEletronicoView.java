package br.com.infox.epp.certificadoeletronico.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.core.util.CollectionUtil;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider.DocumentoComRegraAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.view.AssinaturaCallback;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.certificadoeletronico.CertificadoEletronicoSearch;
import br.com.infox.epp.certificadoeletronico.CertificadoEletronicoService;
import br.com.infox.epp.certificadoeletronico.entity.CertificadoEletronico;
import br.com.infox.epp.documento.type.TipoMeioAssinaturaEnum;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class CertificadoEletronicoView implements Serializable, AssinaturaCallback {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(CertificadoEletronicoView.class);

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

    public AssinavelProvider getAssinavelProvider() {
        return new AssinavelGenericoProvider(
                new DocumentoComRegraAssinatura(TipoMeioAssinaturaEnum.E, UUID.randomUUID().toString().getBytes()));
    }

    @ExceptionHandled(successMessage = "Teste efetuado com sucesso")
    @Override
    public void onSuccess(List<DadosAssinatura> dadosAssinaturaList) {
        if (!CollectionUtil.isEmpty(dadosAssinaturaList)){
            DadosAssinatura dadosAssinatura = dadosAssinaturaList.get(0);
            if (dadosAssinatura == null || dadosAssinatura.getStatus() != StatusToken.SUCESSO) {
                String error = "Erro ao assinar: ";
                error += (dadosAssinatura != null)
                        ? " | " + dadosAssinatura.getCodigoErro() + " - " + dadosAssinatura.getMensagemErro()
                        : "";
                LOG.error(error);
                throw new BusinessRollbackException("Erro ao tentar assinar");
            }
        } else {
            LOG.error("dadosAssinaturaList empty");
            throw new BusinessRollbackException("Erro ao tentar assinar");
        }
    }

    @Override
    public void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinaturaList) {
        if (!CollectionUtil.isEmpty(dadosAssinaturaList)) {
            DadosAssinatura dadosAssinatura = dadosAssinaturaList.get(0);
            String error = "Erro ao assinar: ";
            error += (dadosAssinatura != null)
                    ? " | " + dadosAssinatura.getCodigoErro() + " - " + dadosAssinatura.getMensagemErro()
                    : "";
            LOG.error(error);
        }
        FacesMessages.instance().add(Severity.INFO, "Teste de assinatura falhou");
    }

}