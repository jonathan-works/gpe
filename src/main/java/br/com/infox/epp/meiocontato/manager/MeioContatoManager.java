package br.com.infox.epp.meiocontato.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.meiocontato.dao.MeioContatoDAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(MeioContatoManager.NAME)
public class MeioContatoManager extends Manager<MeioContatoDAO, MeioContato>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "meioContatoManager";

}
