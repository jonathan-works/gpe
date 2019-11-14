package br.com.infox.epp.processo.documento.sigilo.action;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.sigilo.action.SigiloDocumentoController.FragmentoSigilo;
import br.com.infox.epp.processo.documento.sigilo.entity.SigiloDocumento;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.util.collection.Factory;
import br.com.infox.util.collection.LazyMap;

@Named
@ViewScoped
public class SigiloDocumentoAction implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(SigiloDocumentoAction.class);

    @Inject
    private SigiloDocumentoManager sigiloDocumentoManager;
    @Inject
    private SigiloDocumentoController sigiloDocumentoController;
    @Inject
    private ActionMessagesService actionMessagesService;

    private Processo processo;
    private Map<Integer, Boolean> sigiloDocumentoMap;
    private String motivo;

    @PostConstruct
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

    public boolean isSigiloso(Documento documento) {
        return sigiloDocumentoManager.isSigiloso(documento.getId());
    }

    @Transactional
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
    }

    private void resetarMarcacaoSigilo() {
        int idDocumento = sigiloDocumentoController.getDocumentoSelecionado().getId();
        sigiloDocumentoMap.put(idDocumento, !sigiloDocumentoMap.get(idDocumento));
    }
}
