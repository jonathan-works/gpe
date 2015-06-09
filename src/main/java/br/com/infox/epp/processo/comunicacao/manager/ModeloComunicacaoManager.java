package br.com.infox.epp.processo.comunicacao.manager;

import java.util.Collection;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.ModeloComunicacaoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Name(ModeloComunicacaoManager.NAME)
@AutoCreate
public class ModeloComunicacaoManager extends Manager<ModeloComunicacaoDAO, ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoManager";
	
	@In
	private PapelManager papelManager;
	
	public boolean isExpedida(ModeloComunicacao modeloComunicacao) {
		return getDao().isExpedida(modeloComunicacao);
	}
	
	public List<ModeloComunicacao> listModelosComunicacaoPorProcessoRoot(String processoRoot) {
		return getDao().listModelosComunicacaoPorProcessoRoot(processoRoot);
	}
	
	public Processo getComunicacao(DestinatarioModeloComunicacao destinatario) {
		return getDao().getComunicacao(destinatario);
	}
	
	public DocumentoModeloComunicacao getDocumentoInclusoPorPapel(Collection<String> identificadoresPapel, ModeloComunicacao modeloComunicacao) {
		return getDao().getDocumentoInclusoPorPapel(identificadoresPapel, modeloComunicacao);
	}
	
	public List<Documento> getDocumentosByModeloComunicacao(ModeloComunicacao modeloComunicacao){
		return getDao().getDocumentosByModeloComunicacao(modeloComunicacao);
	}
}