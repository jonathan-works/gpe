package br.com.infox.epp.pessoa.documento.dao;

import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PARAM_PESSOA;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PARAM_TPDOCUMENTO;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.documento.type.TipoPesssoaDocumentoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

@AutoCreate
@Name(PessoaDocumentoDAO.NAME)
public class PessoaDocumentoDAO extends DAO<PessoaDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaDocumentoDAO";
	
	public PessoaDocumento searchPessoaDocumentoByPessoaTipoDocumento(Pessoa pessoa, 
			TipoPesssoaDocumentoEnum tipoDocumento) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_PESSOA, pessoa);
		parameters.put(PARAM_TPDOCUMENTO, tipoDocumento);
		return getNamedSingleResult(PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO, parameters);
	}
	
}
