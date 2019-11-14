package br.com.infox.ibpm.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.CollectionUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.view.AssinaturaCallback;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.bean.PastaRestricaoBean;
import br.com.infox.epp.processo.documento.dao.PastaDAO;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.manager.PastaRestricaoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.task.view.FormField;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class DocumentoVariavelController implements Serializable, AssinaturaCallback {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoVariavelController.class);

    @Inject
    private PastaManager pastaManager;
    @Inject
    private PastaRestricaoManager pastaRestricaoManager;
    @Inject
    private PastaDAO pastaDAO;
    @Inject
    private AssinadorService assinadorService;
    @Inject
    private InfoxMessages infoxMessages;

    private Processo processo;
    private List<Pasta> pastas;
    private Pasta pasta;
    private FormField formField;

    public void init(Processo processo, FormField formField) {
        this.processo = processo;
        this.formField = formField;
        pastas = new ArrayList<Pasta>();
        String pastaPadraoFluxo = (String) formField.getProperties().get("pastaPadrao");
        if (pastaPadraoFluxo != null) {
            pasta = pastaManager.getByCodigoAndProcesso(pastaPadraoFluxo, getProcesso());
        }
        if (pasta != null) {
            // se existe pasta especifica na configuracao da variavel atribui ao documento
            pastas.add(pasta);
        } else {
            UsuarioPerfil usuario = Authenticator.getUsuarioPerfilAtual();
            Map<Integer, PastaRestricaoBean> restricoes = pastaRestricaoManager.loadRestricoes(getProcesso(), usuario.getUsuarioLogin(),
                    usuario.getLocalizacao(), usuario.getPerfilTemplate().getPapel());
            for (Integer id : restricoes.keySet()) {
                if (Boolean.TRUE.equals(restricoes.get(id).getWrite())) {
                    Pasta pastaComPermissao = pastaDAO.find(id);
                    pastas.add(pastaComPermissao);
                }
            }
        }
        if (pastas.size() == 1) {
        	pasta = pastas.get(0);
        }
    }

    public AssinavelProvider getAssinavelProvider() {
        return new AssinavelDocumentoBinProvider(TaskInstanceHome.instance().getVariaveisDocumento().get(formField.getId()).getDocumentoBin());
    }

    @Override
    @ExceptionHandled
    public void onSuccess(List<DadosAssinatura> dadosAssinatura) {
        if (CollectionUtil.isEmpty(dadosAssinatura)) {
            FacesMessages.instance().add("Sem documento para assinar");
        } else {
            try {
                assinadorService.assinar(dadosAssinatura, Authenticator.getUsuarioPerfilAtual());
                TaskInstanceHome.instance().setModeloReadonly(formField);
                FacesMessages.instance().add(Severity.INFO, infoxMessages.get("assinatura.assinadoSucesso"));
            } catch (AssinaturaException | DAOException e) {
                FacesMessages.instance().add(e.getMessage());
                LOG.error("assinarDocumento()", e);
            }
        }
    }

    @Override
    @ExceptionHandled
    public void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinatura) {
        FacesMessages.instance().add(Severity.INFO, infoxMessages.get("termoAdesao.sign.fail"));
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public List<Pasta> getPastas() {
        return pastas;
    }

    public void setPastas(List<Pasta> pastas) {
        this.pastas = pastas;
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }

    public boolean isDisableSelectOne() {
        return pasta != null;
    }

    public Pasta getPasta() {
        return pasta;
    }
}
