package br.com.infox.epp.processo.comunicacao.manager;

import java.util.Collection;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.ModeloComunicacaoDAO;
import br.com.infox.epp.processo.entity.Processo;

@Name(ModeloComunicacaoManager.NAME)
@AutoCreate
public class ModeloComunicacaoManager extends Manager<ModeloComunicacaoDAO, ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoManager";
	
	public boolean isExpedida(ModeloComunicacao modeloComunicacao) {
		return getDao().isExpedida(modeloComunicacao);
	}
	
	public List<ModeloComunicacao> listModelosComunicacaoPorProcesso(Processo processo) {
		return getDao().listModelosComunicacaoPorProcesso(processo);
	}
	
	public Processo getComunicacao(DestinatarioModeloComunicacao destinatario) {
		return getDao().getComunicacao(destinatario);
	}
	
	public DocumentoModeloComunicacao getDocumentoInclusoPorPapel(Collection<String> identificadoresPapel, ModeloComunicacao modeloComunicacao) {
		return getDao().getDocumentoInclusoPorPapel(identificadoresPapel, modeloComunicacao);
	}
}