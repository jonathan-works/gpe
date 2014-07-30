package br.com.infox.epp.pessoa.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;

@AutoCreate
@Name(PessoaDocumentoDAO.NAME)
public class PessoaDocumentoDAO extends DAO<PessoaDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaDocumentoDAO";
	
}
