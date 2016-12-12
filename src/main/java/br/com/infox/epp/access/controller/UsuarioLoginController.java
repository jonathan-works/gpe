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
		newInstance();
	}
	
	@Override
	public void onClickFormTab() {
		usuarioLoginCrudAction.onClickFormTab();
	}
	
	public void onClickPessoaFisicaTab() {
		usuarioPessoaFisicaCrudAction.newInstance();
		usuarioPessoaFisicaCrudAction.setUsuarioAssociado(getInstance());
		usuarioPessoaFisicaList.setUsuario(getInstance());
		if (getInstance().getPessoaFisica() != null) {
			meioContatoList.getEntity().setPessoa(getInstance().getPessoaFisica());
			pessoaDocumentoList.getEntity().setPessoa(getInstance().getPessoaFisica());
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
		assinaturaDocumentoList.getEntity().setPessoaFisica(getInstance().getPessoaFisica());
	}
	
	public void onClickVinculoUnidadeDecisoraTab() {
		usuarioLoginCrudAction.limparListasDeUnidadesDecisoras();
	}

	public UsuarioLogin getInstance() {
		return usuarioLoginCrudAction.getInstance();
	}
	
	public void newInstance() {
		setId(null);
		usuarioLoginCrudAction.newInstance();
		usuarioPessoaFisicaCrudAction.newInstance();
		usuarioPessoaFisicaList.newInstance();
		meioContatoList.newInstance();
		pessoaDocumentoList.newInstance();
		usuarioPerfilCrudAction.newInstance();
		usuarioPerfilEntityList.newInstance();
		bloqueioUsuarioCrudAction.newInstance();
		bloqueioUsuarioList.newInstance();
		assinaturaDocumentoList.newInstance();
	}
	
	public void onGravarPessoaFisica() {
		meioContatoList.newInstance();
		pessoaDocumentoList.newInstance();
		meioContatoList.getEntity().setPessoa(getInstance().getPessoaFisica());
		pessoaDocumentoList.getEntity().setPessoa(getInstance().getPessoaFisica());
	}
	
}
