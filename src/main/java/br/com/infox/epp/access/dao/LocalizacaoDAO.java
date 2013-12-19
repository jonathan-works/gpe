package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.LocalizacaoQuery.*;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoDAO.NAME)
@AutoCreate
public class LocalizacaoDAO extends GenericDAO {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "localizacaoDAO";
	
	public List<Localizacao> getLocalizacoesEstrutura(){
	    return getNamedResultList(LOCALIZACOES_ESTRUTURA, null);
	}

}
