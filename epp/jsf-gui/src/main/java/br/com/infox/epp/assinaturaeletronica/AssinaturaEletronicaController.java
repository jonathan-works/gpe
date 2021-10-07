package br.com.infox.epp.assinaturaeletronica;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;


@Named
@ViewScoped
public class AssinaturaEletronicaController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(AssinaturaEletronicaController.class);

    private UploadedFile uploadedFile;
    @Getter
    private AssinaturaEletronicaDTO assinaturaEletronicaAtualDTO = null;
    @Getter
    private AssinaturaEletronicaDTO assinaturaEletronicaNovoDTO = null;
    @Inject
    private AssinaturaEletronicaSearch assinaturaEletronicaSearch;
    @Inject
    private AssinaturaEletronicaService assinaturaEletronicaService;

    @Getter @Setter
    private Integer idPessoaFisica;

    public void newInstance() {
        assinaturaEletronicaAtualDTO = null;
        assinaturaEletronicaNovoDTO = null;
        idPessoaFisica = null;
    }

    public void carregarAssinaturaPessoaFisica(Integer idPessoaFisica) {
        setIdPessoaFisica(idPessoaFisica);
        this.assinaturaEletronicaAtualDTO = assinaturaEletronicaSearch.getAssinaturaEletronicaDTOByIdPessoaFisica(getIdPessoaFisica());
    }

    @ExceptionHandled
    public void uploadAssinaturaEletronica(FileUploadEvent fileUploadEvent) {
        uploadedFile = fileUploadEvent.getUploadedFile();

        try {
            assinaturaEletronicaNovoDTO = new AssinaturaEletronicaDTO();
            assinaturaEletronicaNovoDTO.setIdPessoa(getIdPessoaFisica());
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

    @ExceptionHandled(successMessage = "#{infoxMessages['assinaturaEletronica.imagemSalva']}")
    public void substituirAssinaturaEletronica() {

        if(assinaturaEletronicaNovoDTO == null) {
            throw new BusinessException(InfoxMessages.getInstance().get("assinaturaEletronica.obrigatorio"));
        }
        assinaturaEletronicaService.remove(assinaturaEletronicaAtualDTO);
        assinaturaEletronicaAtualDTO = assinaturaEletronicaService.salvar(assinaturaEletronicaNovoDTO);
        assinaturaEletronicaNovoDTO = null;
    }

    @ExceptionHandled(successMessage = "#{infoxMessages['assinaturaEletronica.imagemRemovida']}")
    public void removerAssinaturaEletronica() {
        assinaturaEletronicaService.remove(assinaturaEletronicaAtualDTO);
        assinaturaEletronicaAtualDTO = null;
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
        return getIdPessoaFisica() != null;
    }

}
