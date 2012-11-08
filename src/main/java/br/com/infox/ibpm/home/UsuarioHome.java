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

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
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
import br.com.infox.ibpm.entity.BloqueioUsuario;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(UsuarioHome.NAME)
@BypassInterceptors
public class UsuarioHome extends AbstractUsuarioHome<Usuario> {

	public static final String AFTER_SET_USUARIO_LOCALIZACAO_ATUAL_EVENT = "br.com.infox.ibpm.home.UsuarioHome.afterSetLocalizacaoAtual";
	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioHome";
	public static final String USUARIO_LOCALIZACAO_ATUAL = "usuarioLogadoLocalizacaoAtual";

	private String login;
	private String password;
	private String passwordConfirm;
	private String email;
	private BloqueioUsuario ultimoBloqueio;
	private BloqueioUsuario novoBloqueio = new BloqueioUsuario();

	/*
	 * Testa se os campos do bloqueio foram preenchidos corretamente J� � feita
	 * uma valida��o no xhtml Essa segunda valida��o (em c�digo) � realmente
	 * necess�ria?
	 */
	private void validarBloqueio() {
		if (getInstance().getBloqueio()
				&& (novoBloqueio.getDataPrevisaoDesbloqueio() == null || novoBloqueio
						.getMotivoBloqueio().equals(""))) {
			getInstance().setBloqueio(false);
			this.novoBloqueio = new BloqueioUsuario();
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					"Campo bloqueio preenchido incorretamente");
		}
	}

	/**
	 * Apaga a data de Expira��o quando o Us�rio passa de Provis�rio para
	 * Permanente
	 * */
	private void validarPermanencia() {
		if (!getInstance().getProvisorio())
			getInstance().setDataExpiracao(null);
	}

	public Usuario checkUserByLogin(String login) {
		Query query = getEntityManager().createNamedQuery(
				UsuarioLogin.USUARIO_LOGIN_NAME);
		query.setParameter(UsuarioLogin.PARAM_LOGIN, login);
		Usuario usu = EntityUtil.getSingleResult(query);
		return usu;
	}

	@Override
	public void newInstance() {
		super.newInstance();
		getInstance().setAtivo(true);
		getInstance().setBloqueio(false);
		getInstance().setLdap(false);
	}

	@Override
	protected Usuario createInstance() {
		Usuario usuario = super.createInstance();
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
			Usuario u = getInstance();
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
		Usuario usuario = getInstance();
		if (usuario.getLogin() == null) {
			usuario.setLogin(login);
		}
		if (estavaBloqueado()) {
			desbloquear();
		}
		if (getInstance().getBloqueio().equals(Boolean.TRUE))
			bloquear();
		return super.update();
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
		if (password == null) {
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

	/**
	 * M�todo que recupera um modelo de documento pelo seu nome e envia
	 * {@link #enviarEmailModelo(ModeloDocumento)}
	 * 
	 * @param nomeModeloDocumento
	 *            Nome do Modelo de Documento a enviar por e-mail
	 * @return true se o e-mail for enviado e false se falhar
	 */
	private boolean enviarModeloPorNome(String nomeModeloDocumento) {
		if (nomeModeloDocumento == null) {
			return false;
		} else if ("false".equals(nomeModeloDocumento)) {
			return false;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select o from ModeloDocumento o");
		sb.append(" where o.tituloModeloDocumento = :titulo");

		ModeloDocumento modelo = (ModeloDocumento) getEntityManager()
				.createQuery(sb.toString())
				.setParameter("titulo", nomeModeloDocumento).getSingleResult();
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

		String nomeModelo = ParametroHome.getParametroOrFalse(nomeParam);

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

		ModeloDocumentoAction action = (ModeloDocumentoAction) Component
				.getInstance(ModeloDocumentoAction.NAME);
		String conteudo = action.getConteudo(modelo);

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
		password = RandomStringUtils.randomAlphabetic(8);
		new RunAsOperation(true) {
			@Override
			public void execute() {
				IdentityManager.instance().changePassword(login, password);
			}
		}.run();

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
		if (email.isEmpty() && login.isEmpty()) {
			FacesMessages.instance().add(
					"� preciso informar o login ou o e-mail do usu�rio");
		} else if (!login.isEmpty()) {
			recoverBy("login", login);
		} else if (!email.isEmpty()) {
			recoverBy("email", email);
		}
	}

	private void recoverBy(String parametro, String valor) {
		// O StringBuilder constr�i a Query com base no parametro passado
		// deixando na forma
		// "select o from Usuario o where o.parametro = :parametro"
		StringBuilder sb = new StringBuilder();
		sb.append("select o from Usuario o where o.");
		sb.append(parametro);
		sb.append(" = :");
		sb.append(parametro);
		Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter(parametro, valor);
		Usuario usuario = (Usuario) query.getSingleResult();
		if (usuario == null) {
			FacesMessages.instance().add("Usu�rio n�o encontrado");
		} else {
			setId(usuario.getIdUsuario());
			gerarNovaSenha(parametro);
		}
	}

	@SuppressWarnings("deprecation")
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