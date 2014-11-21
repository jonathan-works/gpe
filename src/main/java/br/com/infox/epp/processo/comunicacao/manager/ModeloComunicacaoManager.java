package br.com.infox.epp.processo.comunicacao.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.ModeloComunicacaoDAO;

@Name(ModeloComunicacaoManager.NAME)
@AutoCreate
public class ModeloComunicacaoManager extends Manager<ModeloComunicacaoDAO, ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoManager";
	
	public boolean isExpedida(ModeloComunicacao modeloComunicacao) {
		return getDao().isExpedida(modeloComunicacao);
	}
}