package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;


@Name(DownloadComunicacaoAction.NAME)
@AutoCreate
public class DownloadComunicacaoAction implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "downloadComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(DownloadComunicacaoAction.class);
	
	@In
	private GenericManager genericManager;
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private ActionMessagesService actionMessagesService;
	
	public void downloadComunicacaoCompleta(Long idModelo, Long idDestinatario) {
		DestinatarioModeloComunicacao destinatario = null;
		if (idDestinatario != 0 && idDestinatario != null) {
			destinatario = genericManager.find(DestinatarioModeloComunicacao.class, idDestinatario);
		}
		ModeloComunicacao modeloComunicacao = destinatario != null ? destinatario.getModeloComunicacao() : genericManager.find(ModeloComunicacao.class, idModelo);
		try {
			byte[] pdf = comunicacaoService.gerarPdfCompleto(modeloComunicacao, destinatario);
			FileDownloader.download(pdf, "application/pdf", "Comunicação.pdf");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
}
