package br.com.infox.epp.meiocontato.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.meiocontato.dao.MeioContatoDAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.pessoa.entity.Pessoa;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(MeioContatoManager.NAME)
public class MeioContatoManager extends Manager<MeioContatoDAO, MeioContato>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "meioContatoManager";
	
	public List<MeioContato> getByPessoa(Pessoa pessoa) {
		return getDao().getByPessoa(pessoa);
	}
}