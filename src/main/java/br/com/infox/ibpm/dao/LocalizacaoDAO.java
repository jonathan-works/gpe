package br.com.infox.ibpm.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.Localizacao;

@Name(LocalizacaoDAO.NAME)
@AutoCreate
public class LocalizacaoDAO extends GenericDAO {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "localizacaoDAO";
	
	@SuppressWarnings("unchecked")
	public List<Localizacao> getLocalizacoesEstrutura(){
		String hql = "select o from Localizacao o where o.estrutura = true order by o.localizacao";
		return entityManager.createQuery(hql).getResultList();
	}

}
