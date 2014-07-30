package br.com.infox.epp.pessoa.documento.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.pessoa.documento.dao.PessoaDocumentoDAO;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(PessoaDocumentoManager.NAME)
public class PessoaDocumentoManager extends Manager<PessoaDocumentoDAO, PessoaDocumento>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaDocumentoManager";
	
}
