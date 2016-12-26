package br.com.infox.ibpm.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

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
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.bean.PastaRestricaoBean;
import br.com.infox.epp.processo.documento.dao.PastaDAO;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.manager.PastaRestricaoManager;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.task.view.FormField;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named

@ViewScoped
public class DocumentoVariavelController implements Serializable, AssinaturaCallback {

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoVariavelController.class);

	private ProcessoEpaHome processoEpaHome;
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

	private String pastaPadrao;
	private List<Pasta> pastasEditor;
	private List<Pasta> pastasFileUpload;
	private FormField formField;
	private Pasta pastaUpload;
	private Pasta pastaEditor;

	public Pasta consultaPastas(List<Pasta> pastas) {
		processoEpaHome = ProcessoEpaHome.instance();

		if (formField != null) {
			Map<String, Object> properties = formField.getProperties();
			for (String key : properties.keySet()) {
				if (key.equalsIgnoreCase("pastaPadrao")) {
					if (properties.get(key) != null) {
						pastaPadrao = properties.get(key).toString();
						break;
					}
				}
			}
		}

		if (processoEpaHome.getInstance().getIdProcesso() != null) {
			Pasta pasta = pastaManager.getByCodigoAndProcesso(pastaPadrao, processoEpaHome.getInstance());
			if (pasta != null) {
				// se existe pasta especifica na configuracao da variavel atribui ao documento
				pastas.add(pasta);
				return pasta;

			} else {
				return verificaUsuarioTemPermissao(pastas);
			}
		}
		return null;
	}

	private Pasta verificaUsuarioTemPermissao(List<Pasta> pastas) {
		UsuarioPerfil usuario = Authenticator.getUsuarioPerfilAtual();
		Map<Integer, PastaRestricaoBean> restricoes = pastaRestricaoManager.loadRestricoes(
				processoEpaHome.getInstance(), usuario.getUsuarioLogin(), usuario.getLocalizacao(),
				usuario.getPerfilTemplate().getPapel());
		for (Integer id : restricoes.keySet()) {
			if (Boolean.TRUE.equals(restricoes.get(id).getWrite())) {
				Pasta pasta = pastaDAO.find(id);
				pastas.add(pasta);
			}
		}
		if (pastas.size() == 1) {
			return pastas.get(0);
		}

		return null;
	}

	public List<Pasta> getPastasFileUpload() {
		return pastasFileUpload;
	}

	public List<Pasta> getPastasEditor() {
		return pastasEditor;
	}

	public void selecionarPastaPadrao(String variableFieldName) {
		System.out.println("do nothing " + variableFieldName);
	}

	public void setFormFieldFileUpload(FormField formField) {
		this.formField = formField;
		if (pastasFileUpload == null) {
			pastasFileUpload = new ArrayList<Pasta>();
			pastaUpload = consultaPastas(pastasFileUpload);
		}
		setaPastaUpload();
	}

	private void setaPastaUpload() {
		if (pastaUpload != null && formField != null)
			TaskInstanceHome.instance().getVariaveisDocumento().get(formField.getId()).setPasta(pastaUpload);
	}

	public void setFormFieldEditor(FormField formField) {
		this.formField = formField;
		if (pastasEditor == null) {
			pastasEditor = new ArrayList<Pasta>();
			pastaEditor = consultaPastas(pastasEditor);
		}
		setaPastaEditor();
	}

	private void setaPastaEditor() {
		if (pastaEditor != null && formField != null)
			TaskInstanceHome.instance().getVariaveisDocumento().get(formField.getId()).setPasta(pastaEditor);
	}
	
	public AssinavelProvider getAssinavelProvider(){
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
}
