package br.com.infox.epp.processo.documento.sigilo.action;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.sigilo.action.SigiloDocumentoController.FragmentoSigilo;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.util.collection.Factory;
import br.com.infox.util.collection.LazyMap;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(SigiloDocumentoAction.NAME)
@Transactional
public class SigiloDocumentoAction implements Serializable {

    public static final String NAME = "sigiloDocumentoAction";
    private static final long serialVersionUID = 1L;
    private static final String URL_DOWNLOAD_BINARIO = "{0}/downloadDocumento.seam?id={1}";
    private static final String URL_DOWNLOAD_HTML = "{0}/Painel/documentoHTML.seam?id={1}";
    private static final String MOTIVO_COMPONENT_ID = ":visualizarProcessoTabPanel:sigiloDocumentoForm:motivoDecoration:motivo";
    private static final LogProvider LOG = Logging.getLogProvider(SigiloDocumentoAction.class);

    @In
    private SigiloDocumentoManager sigiloDocumentoManager;
    @In
    private SigiloDocumentoController sigiloDocumentoController;
    @In
    private ActionMessagesService actionMessagesService;
    @In
    private PathResolver pathResolver;

    private Processo processo;
    private Map<Integer, Boolean> sigiloDocumentoMap;
    private String motivo;

    @Create
    public void init() {
        this.sigiloDocumentoMap = new LazyMap<>(new Factory<Integer, Boolean>() {
            @Override
            public Boolean create(Integer idDocumento) {
                SigiloDocumentoManager sigiloDocumentoManager = (SigiloDocumentoManager) Component.getInstance(SigiloDocumentoManager.NAME);
                return sigiloDocumentoManager.isSigiloso(idDocumento);
            }
        });
    }

    public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public Map<Integer, Boolean> getSigiloDocumentoMap() {
        return sigiloDocumentoMap;
    }

    public String getViewUrl(Documento documento) {
        if (documento.getDocumentoBin().isBinario()) {
            return MessageFormat.format(URL_DOWNLOAD_BINARIO, pathResolver.getContextPath(), Integer.toString(documento.getId()));
        }
        return MessageFormat.format(URL_DOWNLOAD_HTML, pathResolver.getContextPath(), Integer.toString(documento.getId()));
    }
    
    public boolean isSigiloso(Documento documento) {
        return sigiloDocumentoManager.isSigiloso(documento.getId());
    }

    public void gravarSigiloDocumento() {
        SigiloDocumento sigiloDocumento = new SigiloDocumento();
        sigiloDocumento.setDocumento(sigiloDocumentoController.getDocumentoSelecionado());
        sigiloDocumento.setAtivo(sigiloDocumentoMap.get(sigiloDocumento.getDocumento().getId()));
        sigiloDocumento.setUsuario(Authenticator.getUsuarioLogado());
        sigiloDocumento.setMotivo(motivo);
        sigiloDocumento.setDataInclusao(new Date());
        try {
            sigiloDocumentoManager.persist(sigiloDocumento);
            resetarDados();
            FacesMessages.instance().add(SigiloDocumentoController.MSG_REGISTRO_ALTERADO);
        } catch (DAOException e) {
            LOG.error(e);
            actionMessagesService.handleDAOException(e);
        }
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void prepararGravacaoSigilo(Documento documento) {
        sigiloDocumentoController.setDocumentoSelecionado(documento);
        sigiloDocumentoController.setFragmentoARenderizar(FragmentoSigilo.MOTIVO_SIGILO);
    }

    public void resetarSigiloDocumento() {
        resetarMarcacaoSigilo();
        resetarDados();
    }

    private void resetarDados() {
        this.motivo = null;
        sigiloDocumentoController.setFragmentoARenderizar(null);
        UIInput motivoInput = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent(MOTIVO_COMPONENT_ID);
        motivoInput.resetValue();
    }

    private void resetarMarcacaoSigilo() {
        int idDocumento = sigiloDocumentoController.getDocumentoSelecionado().getId();
        sigiloDocumentoMap.put(idDocumento, !sigiloDocumentoMap.get(idDocumento));
    }
}
