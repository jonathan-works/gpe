package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.exportador.FluxoExporterService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class FluxoDownloader implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(FluxoDownloader.class);
	
	@Inject
    private FluxoExporterService fluxoExporterService;
	@Inject
	private FluxoDAO fluxoDAO;
	
	public void exportarFluxo(Integer idFluxo) {
    	try {
    		Fluxo fluxo = fluxoDAO.find(idFluxo);
    		byte[] zipFile = fluxoExporterService.exportarFluxo(fluxo);
    		FileDownloader.download(zipFile, "application/zip", getFileName(fluxo));
    	} catch (BusinessException e) {
    		FacesMessages.instance().add("Não foi possível exportar o fluxo. " + e.getMessage());
			LOG.error("Erro ao exportar fluxo.", e);
		} catch (Exception e) {
			FacesMessages.instance().add("Não foi possível exportar o fluxo.");
			LOG.error("Erro ao exportar fluxo.", e);
		}
	}
	
	private String getFileName(Fluxo fluxo) {
		return "fluxo" + fluxo.getCodFluxo() + ".epp";
	}
}
