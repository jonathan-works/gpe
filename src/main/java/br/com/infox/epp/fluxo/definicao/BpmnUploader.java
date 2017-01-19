package br.com.infox.epp.fluxo.definicao;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.FileUploadEvent;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.definicao.modeler.BpmnJpdlService;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class BpmnUploader implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(BpmnUploader.class);

	@Inject
	private BpmnJpdlService bpmnJpdlService;
	@Inject
	private DefinicaoProcessoController definicaoProcessoController;
	
	private String bpmn;
	private List<String> mensagens;
	private boolean fluxoImportado;
	
	public void processFileUpload(FileUploadEvent event) {
		try {
			bpmn = IOUtils.toString(event.getUploadedFile().getInputStream());
		} catch (IOException e) {
			LOG.error("Erro no upload", e);
			FacesMessages.instance().add("Erro ao realizar upload: " + e.getMessage());
		}
	}
	
	@ExceptionHandled
	public void importar() {
		try {
			mensagens = new ArrayList<>();
			definicaoProcessoController.setDefinicaoProcesso(bpmnJpdlService.importarBpmn(getDefinicaoProcesso(), bpmn));
			fluxoImportado = true;
			FacesMessages.instance().add("Fluxo importado com sucesso");
		} catch (BusinessException e) {
			mensagens.add(e.getMessage());
		} catch (EJBException e) {
			if (!Strings.isEmpty(e.getCause().getMessage())) {
				mensagens.add(e.getCause().getMessage());
			} else {
				mensagens.add("Houve um erro na importação, contate o administrador do sistema");
			}
		} finally {
			bpmn = null;
		}
	}
	
	public String getBpmn() {
		return bpmn;
	}
	
	public DefinicaoProcesso getDefinicaoProcesso() {
	    return definicaoProcessoController.getDefinicaoProcesso();
	}
	
	public List<String> getMensagens() {
		return mensagens;
	}
	
	public void clearMessages() {
		mensagens = null;
		fluxoImportado = false;
		bpmn = null;
	}
	
	public boolean isFluxoImportado() {
		return fluxoImportado;
	}
}
