package br.com.infox.epp.access.controller;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.access.crud.BloqueioUsuarioCrudAction;
import br.com.infox.epp.access.crud.UsuarioLoginCrudAction;
import br.com.infox.epp.access.crud.UsuarioPerfilCrudAction;
import br.com.infox.epp.access.crud.UsuarioPessoaFisicaCrudAction;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.list.BloqueioUsuarioList;
import br.com.infox.epp.access.list.UsuarioPerfilEntityList;
import br.com.infox.epp.access.list.UsuarioPessoaFisicaList;
import br.com.infox.epp.meiocontato.list.MeioContatoList;
import br.com.infox.epp.pessoa.documento.list.PessoaDocumentoList;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoList;

@Name(UsuarioLoginController.NAME)
@Scope(ScopeType.CONVERSATION)
public class UsuarioLoginController extends AbstractController {
	
	private static final long serialVersionUID = 1L;

	public static final String NAME = "usuarioLoginController";
	
	@In
	private UsuarioLoginCrudAction usuarioLoginCrudAction;
	@In
	private UsuarioPessoaFisicaCrudAction usuarioPessoaFisicaCrudAction;
	@In
	private UsuarioPessoaFisicaList usuarioPessoaFisicaList;
	@In
	private MeioContatoList meioContatoList;
	@In
	private PessoaDocumentoList pessoaDocumentoList;
	@In
	private UsuarioPerfilCrudAction usuarioPerfilCrudAction;
	@In
	private UsuarioPerfilEntityList usuarioPerfilEntityList;
	@In
	private BloqueioUsuarioCrudAction bloqueioUsuarioCrudAction;
	@In
	private BloqueioUsuarioList bloqueioUsuarioList;
	@In
	private AssinaturaDocumentoList assinaturaDocumentoList;
	
	@Override
	public void setId(Object id) {
		super.setId(id);
		usuarioLoginCrudAction.setId(id);
	}
	
	@Override
	public void onClickSearchTab() {
		usuarioLoginCrudAction.onClickSearchTab();
	}
	
	@Override
	public void onClickFormTab() {
		usuarioLoginCrudAction.onClickFormTab();
	}
	
	public void onClickPessoaFisicaTab() {
		UsuarioLogin usuarioLogin = getInstance();
		usuarioPessoaFisicaCrudAction.newInstance();
		usuarioPessoaFisicaCrudAction.setUsuarioAssociado(usuarioLogin);
		usuarioPessoaFisicaList.setUsuario(usuarioLogin);
		PessoaFisica pessoaFisica = usuarioLogin.getPessoaFisica();
		if (pessoaFisica != null) {
			meioContatoList.setPessoa(pessoaFisica);
			pessoaDocumentoList.setPessoa(pessoaFisica);
		}
	}
	
	public void onClickPerfilTab() {
		usuarioPerfilCrudAction.setUsuarioLogin(getInstance());
		usuarioPerfilEntityList.getEntity().setUsuarioLogin(getInstance());
	}
	
	public void onClickBloqueioTab() {
		bloqueioUsuarioCrudAction.setUsuarioAtual(getInstance());
		bloqueioUsuarioList.getEntity().setUsuario(getInstance());
	}

	
	public void onClickAssinaturaTab() {
		assinaturaDocumentoList.getEntity().setUsuario(getInstance());
	}
	
	public void onClickVinculoUnidadeDecisoraTab() {
		usuarioLoginCrudAction.limparListasDeUnidadesDecisoras();
	}

	public UsuarioLogin getInstance() {
		return usuarioLoginCrudAction.getInstance();
	}
	
}
