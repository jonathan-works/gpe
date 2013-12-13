package br.com.infox.epp.system.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.entity.Parametro;

@Name(ParametroManager.NAME)
@AutoCreate
public class ParametroManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parametroManager";
	
	@In private ParametroDAO parametroDAO;
	
	public Parametro getParametro(String nome) {
		return parametroDAO.getParametroByNomeVariavel(nome);
	}

	public void saveSystemParameter(String nomeVariavel,String valorVariavel, String descricaoVariavel) throws DAOException {
        Parametro p = new Parametro();
        p.setNomeVariavel(nomeVariavel);
        p.setValorVariavel(valorVariavel);
        p.setDescricaoVariavel(descricaoVariavel);
        p.setDataAtualizacao(new Date());
        p.setSistema(true);
        p.setAtivo(true);
        persist(p);
    }
	
}
