package br.com.infox.epp.processo.comunicacao.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.joda.time.DateTime;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ProcessoComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.ModeloComunicacaoDAO;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

@Name(ModeloComunicacaoManager.NAME)
@AutoCreate
public class ModeloComunicacaoManager extends Manager<ModeloComunicacaoDAO, ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoManager";
	
	@In
	private DocumentoManager documentoManager;
	@In
	private ProcessoDAO processoDAO;
	
	public void expedirComunicacao(ModeloComunicacao modeloComunicacao) throws DAOException {
		Date dataFimPrazoCiencia = DateTime.now().plusDays(modeloComunicacao.getTipoComunicacao().getQuantidadeDiasCiencia()).toDate();
		Localizacao localizacao = Authenticator.getLocalizacaoAtual();
		UsuarioLogin usuario = Authenticator.getUsuarioLogado();
		for (DestinatarioModeloComunicacao destinatarioModelo : modeloComunicacao.getDestinatarios()) {
			ProcessoComunicacao processo = new ProcessoComunicacao();
			processo.setModeloComunicacao(modeloComunicacao);
			processo.setDataInicio(new Date());
			processo.setDataFimPrazoCiencia(dataFimPrazoCiencia);
			processo.setLocalizacao(localizacao);
			processo.setPrazo(destinatarioModelo.getPrazo());
			processo.setTipoComunicacao(modeloComunicacao.getTipoComunicacao());
			processo.setUsuarioCadastroProcesso(usuario);
			if (destinatarioModelo.getDestinatario() != null) {
				processo.setDestinatario(destinatarioModelo.getDestinatario());
			} else {
				processo.setLocalizacaoDestinataria(destinatarioModelo.getLocalizacaoDestinataria());
			}
			processo.setMeioExpedicao(destinatarioModelo.getMeioExpedicao());
			processo.setNumeroProcesso("");
			processoDAO.persist(processo);
			processo.setNumeroProcesso(processo.getIdProcesso().toString());
			DocumentoBin comunicacao = modeloComunicacao.getComunicacao();
			processo.setComunicacao(documentoManager.createDocumento(processo, comunicacao.getNomeArquivo(), comunicacao, modeloComunicacao.getClassificacaoComunicacao()));
			for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
				Documento documento = documentoModelo.getDocumento();
				processo.getDocumentoList().add(documentoManager.createDocumento(processo, documento.getDescricao(), documento.getDocumentoBin(), documento.getClassificacaoDocumento()));
			}
			processoDAO.update(processo);
		}
		modeloComunicacao.setExpedida(true);
		update(modeloComunicacao);
	}
}
