package br.com.infox.epp.pessoa.documento.dao;

import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PARAM_MATRICULA;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PARAM_PESSOA;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PARAM_TPDOCUMENTO;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO;
import static br.com.infox.epp.pessoa.documento.query.PessoaDocumentoQuery.USUARIO_POR_DOCUMENTO_E_TIPO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.documento.type.TipoPesssoaDocumentoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Stateless
@AutoCreate
@Name(PessoaDocumentoDAO.NAME)
public class PessoaDocumentoDAO extends DAO<PessoaDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaDocumentoDAO";
	
	@Inject
	private PessoaDocumentoSearch pessoaDocumentoSearch;
	
	public PessoaDocumento searchPessoaDocumentoByPessoaTipoDocumento(Pessoa pessoa, 
			TipoPesssoaDocumentoEnum tipoDocumento) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_PESSOA, pessoa);
		parameters.put(PARAM_TPDOCUMENTO, tipoDocumento);
		return getNamedSingleResult(PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO, parameters);
	}

	public UsuarioLogin getUsuarioPorValorDocumentoETipo(String valorDocumento, TipoPesssoaDocumentoEnum dm) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_MATRICULA, valorDocumento);
		parameters.put(PARAM_TPDOCUMENTO, dm);
		return getNamedSingleResult(USUARIO_POR_DOCUMENTO_E_TIPO, parameters);
	}
	
	public void removeAllDocumentosByPessoa(Pessoa pessoa) {
		List<PessoaDocumento> documentos = pessoaDocumentoSearch.getDocumentosByPessoa(pessoa);
		if (documentos != null && !documentos.isEmpty()) {
			for (PessoaDocumento doc : documentos) {
				getEntityManager().remove(doc);
			}
			getEntityManager().flush();
		}
	}
	
	public void adicionaDocumentos(List<PessoaDocumento> documentos) {
		if (documentos != null && !documentos.isEmpty()) {
			for (PessoaDocumento pessoaDocumento : documentos) {
				getEntityManager().persist(pessoaDocumento);
			}
			getEntityManager().flush();
		}
	}

}
