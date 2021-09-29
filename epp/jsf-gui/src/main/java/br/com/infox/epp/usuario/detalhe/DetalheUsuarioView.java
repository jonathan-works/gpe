package br.com.infox.epp.usuario.detalhe;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.access.TermoAdesaoService;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.assinaturaeletronica.AssinaturaEletronicaDTO;
import br.com.infox.epp.assinaturaeletronica.AssinaturaEletronicaSearch;
import br.com.infox.epp.assinaturaeletronica.AssinaturaEletronicaService;
import br.com.infox.epp.assinaturaeletronica.AssinaturaEletronicaServlet;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.certificadoeletronico.CertificadoEletronicoService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DetalheUsuarioView implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final LogProvider LOG = Logging.getLogProvider(DetalheUsuarioView.class);

    @Getter
    @Setter
    private String tab = "info";
    @Getter
    private String nome;
    @Getter
    private String termo;
    @Getter
    private String urlTermoAdesao;
    @Getter
    private Date dataInicio;
    @Getter
    private Date dataFim;
    @Inject
    private TermoAdesaoService termoAdesaoService;
    @Inject
    private CertificadoEletronicoService certificadoEletronicoService;
    @Getter
    private boolean possuiCertificadoEmitido = false;
    @Getter
    private boolean podeEmitirCertificado = false;
    @Getter
    private boolean podeExibirTermoAdesao = false;

    private UploadedFile uploadedFile;

    @Getter
    private AssinaturaEletronicaDTO assinaturaEletronicaAtualDTO = null;
    @Getter
    private AssinaturaEletronicaDTO assinaturaEletronicaNovoDTO = null;
    @Inject
    private AssinaturaEletronicaSearch assinaturaEletronicaSearch;
    @Inject
    private AssinaturaEletronicaService assinaturaEletronicaService;

    @PostConstruct
    private void init() {
        possuiCertificadoEmitido = false;
        podeEmitirCertificado = false;
        podeExibirTermoAdesao = false;
        refreshInitValues();
    }

    private void refreshInitValues() {
        UsuarioLogin usuario = Authenticator.getUsuarioLogado();
        this.nome = usuario.getNomeUsuario();
        this.podeExibirTermoAdesao = usuario.getPessoaFisica() != null
                && usuario.getPessoaFisica().getTermoAdesao() != null;
        if (usuario.getPessoaFisica() != null) {
            this.assinaturaEletronicaAtualDTO = assinaturaEletronicaSearch.getAssinaturaEletronicaDTOByIdPessoaFisica(usuario.getPessoaFisica().getIdPessoa());
            this.possuiCertificadoEmitido = usuario.getPessoaFisica().getCertificadoEletronico() != null;
            this.podeEmitirCertificado = !this.possuiCertificadoEmitido
                    || !usuario.getPessoaFisica().getCertificadoEletronico().isAtivo();
            if (this.possuiCertificadoEmitido) {
                this.dataInicio = usuario.getPessoaFisica().getCertificadoEletronico().getDataInicio();
                this.dataFim = usuario.getPessoaFisica().getCertificadoEletronico().getDataFim();
            }
        }
        if (isPodeExibirTermoAdesao()) {
            this.termo = usuario.getPessoaFisica().getTermoAdesao().getModeloDocumento();
            this.urlTermoAdesao = termoAdesaoService.buildUrlDownload(getHttpServletRequest().getContextPath(), null,
                    usuario.getPessoaFisica().getTermoAdesao().getUuid().toString());

        }
    }

    @ExceptionHandled(successMessage = "Certificado gerado com sucesso")
    public void gerarCertificado() {
        certificadoEletronicoService.gerarCertificado(Authenticator.getUsuarioLogado().getPessoaFisica());
        refreshInitValues();
    }

    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    @ExceptionHandled
    public void uploadAssinaturaEletronica(FileUploadEvent fileUploadEvent) {
        uploadedFile = fileUploadEvent.getUploadedFile();

        try {
            assinaturaEletronicaNovoDTO = new AssinaturaEletronicaDTO();
            assinaturaEletronicaNovoDTO.setIdPessoa(getIdPessoaFisicaLogada());
            assinaturaEletronicaNovoDTO.setExtensao(uploadedFile.getFileExtension());
            assinaturaEletronicaNovoDTO.setNomeArquivo(uploadedFile.getName());
            assinaturaEletronicaNovoDTO.setContentType(uploadedFile.getContentType());
            assinaturaEletronicaNovoDTO.setImagem(ArrayUtil.copyOf(uploadedFile.getData()));
        } finally {
            limparUploadFile();
        }
    }

    @ExceptionHandled
    public void limpaUploadAssinaturaEletronica() {
        assinaturaEletronicaNovoDTO = null;
        limparUploadFile();
    }

    private void limparUploadFile() {
        if(uploadedFile != null) {
            try {
                uploadedFile.delete();
            } catch (IOException e) {
                LOG.error(".limpaUploadAssinaturaEletronica", e);
            }
        }
    }

    @ExceptionHandled(successMessage = "#{infoxMessages['assinaturaEletronica.imagemSalva']}")
    public void salvarAssinaturaEletronica() {

        if(assinaturaEletronicaNovoDTO == null) {
            throw new BusinessException(InfoxMessages.getInstance().get("assinaturaEletronica.obrigatorio"));
        }

        assinaturaEletronicaAtualDTO = assinaturaEletronicaService.salvar(assinaturaEletronicaNovoDTO);
        assinaturaEletronicaNovoDTO = null;
    }

    @ExceptionHandled(successMessage = "#{infoxMessages['assinaturaEletronica.imagemRemovida']}")
    public void removerAssinaturaEletronica() {
        assinaturaEletronicaService.remove(assinaturaEletronicaAtualDTO);
        assinaturaEletronicaAtualDTO = null;
    }

    private Integer getIdPessoaFisicaLogada() {
        UsuarioLogin usuario = Authenticator.getUsuarioLogado();
        return usuario.getPessoaFisica().getIdPessoa();
    }

    public String getLinkAssinaturaEletronica() {
        String link = "";

        if(assinaturaEletronicaAtualDTO != null) {
            link = String.format("%s%s%s", FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath(),
                    AssinaturaEletronicaServlet.SERVLET_BASE_URL, assinaturaEletronicaAtualDTO.getUuid().toString());
        }
        return link;
    }

    public boolean podeConfigurarAssinaturaEletronica() {
        UsuarioLogin usuario = Authenticator.getUsuarioLogado();
        return usuario.getPessoaFisica() != null;
    }

}