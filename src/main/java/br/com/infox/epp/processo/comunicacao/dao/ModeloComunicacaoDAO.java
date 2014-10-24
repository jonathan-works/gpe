package br.com.infox.epp.processo.comunicacao.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;

@Name(ModeloComunicacaoDAO.NAME)
@AutoCreate
public class ModeloComunicacaoDAO extends DAO<ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoDAO";
}
