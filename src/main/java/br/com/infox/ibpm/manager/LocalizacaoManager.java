package br.com.infox.ibpm.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.LocalizacaoDAO;
import br.com.infox.ibpm.entity.Localizacao;

@Name(LocalizacaoManager.NAME)
@AutoCreate
public class LocalizacaoManager extends GenericManager {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "localizacaoManager";
	@In private LocalizacaoDAO localizacaoDAO;
	
	public List<Localizacao> getLocalizacoesEstrutura(){
		return localizacaoDAO.getLocalizacoesEstrutura();
	}

}
