/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
 */
package br.com.infox.ibpm.home;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.security.auth.login.LoginException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.PasswordHash;
import org.jboss.seam.util.RandomStringUtils;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.command.EMailData;
import br.com.infox.command.SendmailCommand;
import br.com.infox.epp.manager.ModeloDocumentoManager;
import br.com.infox.ibpm.entity.BloqueioUsuario;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.infox.ibpm.manager.PessoaManager;
import br.com.infox.ibpm.manager.UsuarioLoginManager;
import br.com.infox.util.ParametroUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(UsuarioHome.NAME)
public class UsuarioHome extends AbstractUsuarioHome<UsuarioLogin> {

	private static final int PASSWORD_LENGTH = 8;
    public static final String AFTER_SET_USUARIO_LOCALIZACAO_ATUAL_EVENT = "br.com.infox.ibpm.home.UsuarioHome.afterSetLocalizacaoAtual";
	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioHome";
	public static final String USUARIO_LOCALIZACAO_ATUAL = "usuarioLogadoLocalizacaoAtual";
	
	@In private UsuarioLoginManager usuarioLoginManager;
	@In private PessoaManager pessoaManager;
	@In private ModeloDocumentoManager modeloDocumentoManager;

	private String login;
	private String password;
	private String passwordConfirm;
	private String email;
	private BloqueioUsuario ultimoBloqueio;
	private BloqueioUsuario novoBloqueio = new BloqueioUsuario();
	private boolean pessoaFisicaCadastrada = false;

	/*
	 * Testa se os campos do bloqueio foram preenchidos corretamente 
	 * J� � feita uma valida��o no xhtml 
	 * Essa segunda valida��o (em c�digo) � realmente necess�ria?
	 */
	private void validarBloqueio() {
		if (getInstance().getBloqueio() && (novoBloqueio.getDataPrevisaoDesbloqueio() == null || novoBloqueio.getMotivoBloqueio().equals(""))) {
			getInstance().setBloqueio(false);
			this.novoBloqueio = new BloqueioUsuario();
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					"Campo bloqueio preenchido incorretamente");
		}
	}

	/**
	 * Apaga a data de Expira��o quando o Us�rio passa de Provis�rio para Permanente
	 * */
	private void validarPermanencia() {
		if (!getInstance().getProvisorio()) {
		    getInstance().setDataExpiracao(null);
		}
	}

	public UsuarioLogin checkUserByLogin(String login) {
		Query query = getEntityManager().createNamedQuery(
				UsuarioLogin.USUARIO_LOGIN_NAME);
		query.setParameter(UsuarioLogin.PARAM_LOGIN, login);
		return EntityUtil.getSingleResult(query);
	}

	@Override
	public void newInstance() {
		super.newInstance();
		getInstance().setAtivo(true);
		getInstance().setBloqueio(false);
		getInstance().setLdap(false);
	}

	@Override
	protected UsuarioLogin createInstance() {
		UsuarioLogin usuario = super.createInstance();
		usuario.setAtivo(true);
		usuario.setLdap(false);
		return usuario;
	}

	@Override
	public void setId(Object id) {
		boolean changed = id != null && !id.equals(getId());
		super.setId(id);
		if (changed) {
			ultimoBloqueio = null;
			UsuarioLogin u = getInstance();
			login = getInstance().getLogin();
			List<BloqueioUsuario> bloqueioUsuarioList = u
					.getBloqueioUsuarioList();
			if (!bloqueioUsuarioList.isEmpty()) {
				int i = bloqueioUsuarioList.size() - 1;
				ultimoBloqueio = bloqueioUsuarioList.get(i);
			}
		}
	}

	/*
	 * Regra do Bloqueio
	 * 
	 * Caso 1: Bloquear usu�rio j� Bloqueado { 1.1: Bloqueio antigo � desfeito
	 * 1.2: Novo bloqueio � criado } Caso 2: Bloquear usu�rio ativo 2.1: Novo
	 * bloqueio � criado
	 */
	@Override
	public String update() {
		validarBloqueio();
		validarPermanencia();
		UsuarioLogin usuario = getInstance();
		if (usuario.getLogin() == null) {
			usuario.setLogin(login);
		}
		if (estavaBloqueado()) {
			desbloquear();
		}
		if (getInstance().getBloqueio().equals(Boolean.TRUE)) {
		    bloquear();
		}
		return super.update();
	}
	
	public String updateSemWiacs() {
		return super.update();
	}

	@Override
	protected boolean beforePersistOrUpdate() {
		login = getInstance().getLogin();
		return login != null;
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		if (instance.getSenha() == null) {
			password = instance.getSenha();
			gerarNovaSenha();
		}
		return ret;
	}

	public String persist(boolean senha) {
		login = getInstance().getLogin();
		String resultado = super.persist();
		if (senha) {
			gerarNovaSenha();
		}
		return resultado;
	}
	
	@Override
	public String persist() {
		login = getInstance().getLogin();
		String resultado;
		if (!pessoaFisicaCadastrada){
			resultado = super.persist();
		} else{
			usuarioLoginManager.inserirUsuarioParaPessoaFisicaCadastrada(login, instance);
			instance = usuarioLoginManager.getUsuarioLogin(instance);
			// TODO: Verificar se funciona com getEntityManager.detach()
			resultado = "persisted";
		}
		return resultado;
	}

	public boolean estavaBloqueado() {
		if (ultimoBloqueio != null) {
			return (ultimoBloqueio.getDataDesbloqueio() == null);
		}
		return false;
	}

	public void desbloquear() {
		ultimoBloqueio.setDataDesbloqueio(new Date());
		super.update();
	}

	public void bloquear() {
		novoBloqueio.setDataBloqueio(new Date());
		novoBloqueio.setUsuario(getInstance());
		getInstance().getBloqueioUsuarioList().add(novoBloqueio);
		ultimoBloqueio = novoBloqueio;
		EntityUtil.getEntityManager().persist(ultimoBloqueio);
		novoBloqueio = new BloqueioUsuario();
	}

	/**
	 * M�todo que recupera um modelo de documento pelo seu nome e envia
	 * {@link #enviarEmailModelo(ModeloDocumento)}
	 * 
	 * @param nomeModeloDocumento
	 *            Nome do Modelo de Documento a enviar por e-mail
	 * @return true se o e-mail for enviado e false se falhar
	 */
	private boolean enviarModeloPorNome(String nomeModeloDocumento) {
		if (nomeModeloDocumento == null || "false".equals(nomeModeloDocumento)) {
			return false;
		}
		ModeloDocumento modelo = modeloDocumentoManager.getModeloDocumentoByTitulo(nomeModeloDocumento);
		if (modelo == null) {
			return false;
		} else {
			enviarEmailModelo(modelo);
		}

		return true;
	}

	/**
	 * Inicia o processo de requisi��o de senha
	 * 
	 * Requisita nova senha baseada na informa��o fornecida pelo usu�rio e tenta
	 * enviar com base na informa��o recuperada
	 * {@link #enviarModeloPorNome(String)}.
	 * 
	 * TODO:melhorar nome do m�todo
	 * 
	 * @param parametro
	 *            Tipo da requisi��o de senha
	 */
	private void iniciarRequisicao(String parametro) {
		String nomeParam = null;
		if ("login".equals(parametro)) {
			nomeParam = "tituloModeloEmailMudancaSenha";
		} else if ("email".equals(parametro)) {
			nomeParam = "tituloModeloEmailMudancaSenhaComLogin";
		}

		String nomeModelo = ParametroUtil.getParametroOrFalse(nomeParam);

		if (!enviarModeloPorNome(nomeModelo)) {
			FacesMessages.instance().add(
					StatusMessage.Severity.ERROR,
					"Erro no envio do e-mail. O par�metro de sistema '"
							+ nomeParam
							+ "' n�o foi definido ou possui um valor inv�lido");
		}
	}

	/**
	 * Envia e-mail baseado em um Modelo de Documento
	 * 
	 * @param modelo
	 *            Modelo do e-mail a ser enviado
	 */
	private void enviarEmailModelo(ModeloDocumento modelo) {
		if (modelo == null) {
			return;
		}
		pessoaFisicaCadastrada = false;
		String conteudo = ModeloDocumentoAction.instance().getConteudo(modelo);

		EMailData data = ComponentUtil.getComponent(EMailData.NAME);
		data.setUseHtmlBody(true);
		data.setBody(conteudo);
		data.getRecipientList().clear();
		data.getRecipientList().add(getInstance());
		data.setSubject("Senha do Sistema");
		FacesMessages.instance().add("Senha gerada com sucesso.");
		new SendmailCommand().execute("/WEB-INF/email/emailTemplate.xhtml");
	}

	public void gerarNovaSenha(String parametro) {
		password = RandomStringUtils.randomAlphabetic(PASSWORD_LENGTH);
		getInstance().setSenha(password);
		new RunAsOperation(true) {
			@Override
			public void execute() {
				IdentityManager.instance().changePassword(login, password);
			}
		}.run();
		
		EntityUtil.getEntityManager().flush();
		
		iniciarRequisicao(parametro);
	}

	public void gerarNovaSenha() {
		gerarNovaSenha("email");
	}

	public void gerarSenhaInicial() {
		new RunAsOperation(true) {
			@Override
			public void execute() {
				IdentityManager.instance().changePassword(
						getInstance().getLogin(), getInstance().getLogin());
			}
		}.run();
	}

	/**
	 * Metodo que gera uma nova senha para usu�rio. Este metodo faz isso
	 * buscando na base do wiacs o usu�rio pelo login e email e retorna uma
	 * mensagem de erro caso n�o encontre. A partir do usu�rio do wiacs � dado
	 * um setId utilizando a 'identificacao'.
	 * 
	 * @throws LoginException
	 */
	public void requisitarNovaSenha() throws LoginException {
		UsuarioLogin usuario;
		if (email.isEmpty() && login.isEmpty()) {
			FacesMessages.instance().add(
					"� preciso informar o login ou o e-mail do usu�rio");
			return;
		} else if (!login.isEmpty()) {
			usuario = usuarioLoginManager.getUsuarioLoginByLogin(login);
			recoverUsuario(usuario, login);
		} else if (!email.isEmpty()) {
			usuario = usuarioLoginManager.getUsuarioLoginByEmail(email);
			recoverUsuario(usuario, email);
		}
	}

	private void recoverUsuario(UsuarioLogin usuario, String parametro) {
		if (usuario == null) {
			FacesMessages.instance().add("Usu�rio n�o encontrado");
		} else {
			setId(usuario.getIdPessoa());
			gerarNovaSenha(parametro);
		}
	}

	public static String gerarHashSenha(UsuarioLogin usuarioLogin) {
		String hash = new PasswordHash().generateSaltedHash(
				usuarioLogin.getLogin(), usuarioLogin.getLogin(), "SHA");
		return hash;
	}

	public static UsuarioHome instance() {
		return ComponentUtil.getComponent("usuarioHome");
	}

	/**
	 * Atalho para a localiza��o atual
	 * 
	 * @return a localiza��o atual do usu�rio
	 */
	public static UsuarioLocalizacao getUsuarioLocalizacaoAtual() {
		return (UsuarioLocalizacao) Contexts.getSessionContext().get(
				USUARIO_LOCALIZACAO_ATUAL);
	}
	
	public void searchByCpf(String cpf){
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioLoginByCpf(cpf);
		if (usuarioLogin == null){
			PessoaFisica pessoaFisica = pessoaManager.getPessoaFisicaByCpf(cpf);
			if (pessoaFisica != null){
				pessoaFisicaCadastrada = true;
				instance = getInstance().loadDataFromPessoaFisica(pessoaFisica);
			}
			else {
				pessoaFisicaCadastrada = false;
				}
		} else{
			instance = usuarioLogin;
		}
	}
	
	// ----Getters e Setters----
	public BloqueioUsuario getUltimoBloqueio() {
		return ultimoBloqueio;
	}

	public BloqueioUsuario getNovoBloqueio() {
		return novoBloqueio;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}