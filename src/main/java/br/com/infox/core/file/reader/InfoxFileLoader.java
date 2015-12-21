package br.com.infox.core.file.reader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@RequestScoped
public class InfoxFileLoader implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private DocumentoBinarioManager documentoBinarioManager;

	private LogProvider LOG = Logging.getLogProvider(InfoxFileLoader.class);

	public StreamedContent getMedia() {
		Integer idDocumentoBin;
		Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String paramIdDocumento = requestParameterMap.get("idDocumentoBin");
		if (paramIdDocumento != null && !paramIdDocumento.isEmpty()) {
			idDocumentoBin = Integer.parseInt(paramIdDocumento);
			try {
				DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
				byte[] data = documentoBinarioManager.getData(idDocumentoBin);
				InputStream inputStream = new ByteArrayInputStream(data);
				if (documentoBin != null && data != null) {
					return new DefaultStreamedContent(inputStream, documentoBin.getExtensao(), documentoBin.getNomeArquivo(), "UTF-8");
				}
			} catch (Exception e) {
				LOG.error("Erro ao tentar carregar arquivo.", e);
			}
		}
		return new DefaultStreamedContent();
	}
}
