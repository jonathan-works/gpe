package br.com.infox.epa.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.NaturezaLocalizacaoDAO;
import br.com.infox.epa.entity.Natureza;
import br.com.infox.epa.entity.NaturezaLocalizacao;

@Name(NaturezaLocalizacaoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NaturezaLocalizacaoManager extends GenericManager {

	private static final long serialVersionUID = 2985338050214816668L;

	public static final String NAME = "naturezaLocalizacaoManager";

	@In
	private NaturezaLocalizacaoDAO naturezaLocalizacaoDAO;
	
	public List<NaturezaLocalizacao> listByNatureza(Natureza natureza) {
		return naturezaLocalizacaoDAO.listByNatureza(natureza);
	}
	
}