package br.com.infox.epp.fluxo.crud;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.FileUploadEvent;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.exportador.FluxoConfiguration;
import br.com.infox.epp.fluxo.importador.FluxoImporterService;
import br.com.infox.epp.modeler.converter.BpmnJpdlService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.ValidationException;

@Named
@ViewScoped
public class FluxoUploader implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(FluxoUploader.class);
	
	@Inject
	private BpmnJpdlService bpmnJpdlService;
	@Inject
	private FluxoImporterService fluxoImporterService;
	
	private Fluxo fluxo;
	private List<String> mensagens;
	private boolean fluxoImportado;
	private boolean tipoImportBpmn = true;
	private String bpmn;
	private HashMap<String, String> eppFluxoFile;
    private byte[] buffer = new byte[1024];
	
	public void processFileUpload(FileUploadEvent event) {
		try {
			if (isTipoImportBpmn()) {
				bpmn = IOUtils.toString(event.getUploadedFile().getInputStream());
			} else {
				eppFluxoFile = new HashMap<>();
				ZipInputStream zis = new ZipInputStream(event.getUploadedFile().getInputStream());
				ZipEntry ze = zis.getNextEntry();
				while (ze!= null) {
					ByteArrayOutputStream fos = new ByteArrayOutputStream();             
		            int len;
		            while ((len = zis.read(buffer)) > 0) {
		            	fos.write(buffer, 0, len);
		            }
		            fos.close();
		            eppFluxoFile.put(ze.getName(), fos.toString());
					ze = zis.getNextEntry();
				}
				validaEppFluxoFile();
			}
			clearMessages();
		} catch (IOException | BusinessException e) {
			LOG.error("Erro no upload", e);
			FacesMessages.instance().add("Erro ao realizar upload: " + e.getMessage());
		}
	}

	private void validaEppFluxoFile() {
		if (!eppFluxoFile.containsKey(FluxoConfiguration.FLUXO_XML)) {
			throw new BusinessException("Não foi encontrado o arquivo com a definição de fluxo.");
		}
		if (!eppFluxoFile.containsKey(FluxoConfiguration.PROCESS_VARIABLES_XML)) {
			throw new BusinessException("Não foi encontrado o arquivo com as definições de variavéis de processo.");
		}
	}
	
	@ExceptionHandled
	public void importar() {
		try {
			if (isTipoImportBpmn()) {
				fluxo = bpmnJpdlService.importarBpmn(fluxo, bpmn);
			} else {
				fluxo = fluxoImporterService.importarFluxo(eppFluxoFile, fluxo);
			}
			fluxoImportado = true;
			FacesMessages.instance().add("Fluxo importado com sucesso");
		} catch (ValidationException e) {
			mensagens = new ArrayList<>(e.getValidationMessages());
		} catch (BusinessException e) {
			mensagens = new ArrayList<>();
			mensagens.add(e.getMessage());
		} finally {
			bpmn = null;
			eppFluxoFile = null;
		}
	}
	
	public boolean podeImportar() {
		return fluxo != null && (isTipoImportBpmn() && bpmn != null || !isTipoImportBpmn() && eppFluxoFile != null);
	}
	
	public void clearMessages() {
		mensagens = null;
		fluxoImportado = false;
	}
	
	public boolean isFluxoImportado() {
		return fluxoImportado;
	}
	
	public Fluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	public List<String> getMensagens() {
		return mensagens;
	}

	public boolean isTipoImportBpmn() {
		return tipoImportBpmn;
	}

	public void setTipoImportBpmn(boolean tipoImportBpmn) {
		this.tipoImportBpmn = tipoImportBpmn;
	}
	
	public String getAcceptedTypes() {
		return isTipoImportBpmn() ? ".bpmn" : ".epp";
	}
}
