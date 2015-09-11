package br.com.infox.epp.endereco.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.endereco.dao.EnderecoDAO;
import br.com.infox.epp.endereco.entity.Endereco;

@Name(EnderecoManager.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
public class EnderecoManager extends Manager<EnderecoDAO, Endereco> {
	
    public static final String NAME = "enderecoManager";
    private static final long serialVersionUID = 1L;

}
