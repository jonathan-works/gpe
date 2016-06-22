package br.com.infox.epp.fluxo.bpmn;

import java.io.IOException;
import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.FileUploadEvent;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.modeler.converter.BpmnJpdlService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class BpmnUploader implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(BpmnUploader.class);

	@Inject
	private BpmnJpdlService bpmnJpdlService;
	
	private String bpmn;
	private Fluxo fluxo;
	
	public void processFileUpload(FileUploadEvent event) {
		try {
			bpmn = IOUtils.toString(event.getUploadedFile().getInputStream());
		} catch (IOException e) {
			LOG.error("Erro no upload", e);
			FacesMessages.instance().add("Erro ao realizar upload: " + e.getMessage());
		}
	}
	
	@ExceptionHandled(successMessage = "Fluxo importado com sucesso")
	public void importar() {
		try {
			fluxo = bpmnJpdlService.importarBpmn(fluxo, bpmn);
		} finally {
			bpmn = null;
		}
	}
	
	public String getBpmn() {
		return bpmn;
	}
	
	public Fluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
}
