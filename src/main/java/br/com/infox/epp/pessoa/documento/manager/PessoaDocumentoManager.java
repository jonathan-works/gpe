package br.com.infox.epp.pessoa.documento.manager;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.pessoa.documento.dao.PessoaDocumentoDAO;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.documento.type.TipoPesssoaDocumentoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(PessoaDocumentoManager.NAME)
@Stateless
public class PessoaDocumentoManager extends Manager<PessoaDocumentoDAO, PessoaDocumento>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaDocumentoManager";
	
	public PessoaDocumento getPessoaDocumentoByPessoaTipoDocumento(Pessoa pessoa, 
			TipoPesssoaDocumentoEnum tipoDocumento){
		return getDao().searchPessoaDocumentoByPessoaTipoDocumento(pessoa, tipoDocumento);
	}

	public UsuarioLogin getUsuarioByMatricula(String valorMatricula) {
		return getDao().getUsuarioPorValorDocumentoETipo(valorMatricula, TipoPesssoaDocumentoEnum.DM);
	}
	
	
}
