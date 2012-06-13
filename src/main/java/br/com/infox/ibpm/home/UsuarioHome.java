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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.security.auth.login.LoginException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
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
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name("usuarioHome")
@BypassInterceptors
public class UsuarioHome extends AbstractUsuarioHome<Usuario> {

	public static final String AFTER_SET_USUARIO_LOCALIZACAO_ATUAL_EVENT = "br.com.infox.ibpm.home.UsuarioHome.afterSetLocalizacaoAtual";
	private static final long serialVersionUID = 1L;
	public static final String USUARIO_LOCALIZACAO_ATUAL = "usuarioLogadoLocalizacaoAtual";
	
	private String login;
	private String password;
	private String passwordConfirm;
	private String email;
	private BloqueioUsuario ultimoBloqueio;
	private BloqueioUsuario novoBloqueio = new BloqueioUsuario();
	private boolean estavaBloqueado;
	
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

	private void validarBloqueio(){
		if (getInstance().getBloqueio() &&
				(novoBloqueio.getDataPrevisaoDesbloqueio() == null ||
				novoBloqueio.getMotivoBloqueio().equals(""))){
			getInstance().setBloqueio(false);
			this.novoBloqueio = new BloqueioUsuario();
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
				"Campo bloqueio preenchido incorretamente");
		}
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		getInstance().setAtivo(true);
		getInstance().setBloqueio(false);
	}
	
	@Override
	protected Usuario createInstance() {
		Usuario usuario = super.createInstance();
		usuario.setAtivo(true);		
		return usuario;
	}
	
	@Override
	public void setId(Object id) {
		boolean changed = id != null && !id.equals(getId());
		super.setId(id);
		if (changed){
			ultimoBloqueio = null;
			Usuario u = getInstance();
			login = getInstance().getLogin();
			List<BloqueioUsuario> bloqueioUsuarioList = u.getBloqueioUsuarioList();
			if (!bloqueioUsuarioList.isEmpty()){
				int i = bloqueioUsuarioList.size() - 1;
				ultimoBloqueio = bloqueioUsuarioList.get(i);
			}
			if (Boolean.TRUE.equals(u.getBloqueio())) {
				estavaBloqueado = true;
			}			
		}
	}
	
	@Override
	public String update() {
		validarBloqueio();
		Usuario usuario = getInstance();
		if (usuario.getLogin() == null) {
			usuario.setLogin(login);
		}
		String saida = super.update();
		if ((novoBloqueio != null) && (novoBloqueio.getDataPrevisaoDesbloqueio() != null)){
			novoBloqueio.setDataBloqueio(new Date());
			novoBloqueio.setUsuario(getInstance());
			getInstance().getBloqueioUsuarioList().add(novoBloqueio);
			ultimoBloqueio = novoBloqueio;
			novoBloqueio = new BloqueioUsuario();
		}
		if (estavaBloqueado && getInstance().getBloqueio().equals(Boolean.FALSE)) {
			ultimoBloqueio.setDataDesbloqueio(new Date());
		}

		return saida;		
	}
	
	public String updateSemWiacs() {
		return super.update();
	}
	
	
	@Override
	public String persist() {
		login = getInstance().getLogin();
		//TODO provisorio, criar usuario com senha igual ao login
		getInstance().setSenha(getInstance().getLogin());
		String resultado = super.persist();
		if (password == null) {
			gerarNovaSenha();
		}		
		return resultado;
	}
	
	private void enviarEmail() {
		//obter o id do modelo do contexto da aplica��o
		Object objIdModelo = Contexts.getApplicationContext().get("idModeloEMailMudancaSenha");
		FacesMessages fm = FacesMessages.instance();
		if (objIdModelo != null) {
			String idModelo = objIdModelo.toString(); 
			ModeloDocumento modelo = getEntityManager().find(ModeloDocumento.class, new Integer(idModelo));
			if (modelo != null) {
				String body = modelo.getModeloDocumento();
				//processa o modelo avaliando as express�es
				body = (String) Expressions.instance().createValueExpression(body).getValue();
				
				// busca o componente para ser utilizado no template de email
				EMailData data = ComponentUtil.getComponent(EMailData.NAME);
				data.setUseHtmlBody(true);
				data.setBody(body);
				data.getRecipientList().clear();
				data.getRecipientList().add(getInstance());
				data.setSubject("Senha do Sistema");
				fm.add("Senha gerada com sucesso.");
				new SendmailCommand().execute("/WEB-INF/email/emailTemplate.xhtml");
			} else {
				fm.add(StatusMessage.Severity.ERROR, "Erro no envio do e-mail. O par�metro de sistema " +
				"'idModeloEMailMudancaSenha' est� definido com valor inv�lido");				
			}
		} else {
			fm.add(StatusMessage.Severity.ERROR, "Erro no envio do e-mail. O par�metro de sistema " +
					"'idModeloEMailMudancaSenha' n�o foi definido");
		}
	}
	
	public void gerarNovaSenha() {
		password = RandomStringUtils.randomAlphabetic(8); 
		new RunAsOperation(true) {
			public void execute() {
				IdentityManager.instance().changePassword(login, password);
			}
		}.run();
		getInstance().setProvisorio(true);
		EntityManager em = EntityUtil.getEntityManager();
		em.merge(getInstance());
		em.flush();		
		enviarEmail();
	}
	
	public void gerarSenhaInicial() {
		new RunAsOperation(true) {
			public void execute() {
				IdentityManager.instance().changePassword(getInstance().getLogin(), getInstance().getLogin());
			}
		}.run();
	}
	
	/**
	 * Metodo que gera uma nova senha para usu�rio. Este metodo faz isso buscando 
	 * na base do wiacs o usu�rio pelo login e email e retorna uma mensagem de erro caso
	 * n�o encontre. A partir do usu�rio do wiacs � dado um setId utilizando a 'identificacao'.
	 * @throws LoginException 
	 */
	@SuppressWarnings("unchecked")
	public void requisitarNovaSenha() throws LoginException {
		FacesMessages fm = FacesMessages.instance();
		if (email == null || login == null) {
			fm.add("� preciso informar o login e o e-mail do usu�rio");
		} else {
			String hsql = "select o from Usuario o " +
				"where o.login = :login and o.email = :email";
			Query query = getEntityManager().createQuery(hsql);
			query.setParameter("login", login);
			query.setParameter("email", email);
			List<Usuario> list = query.getResultList();
			if (list.size() == 0) {
				fm.add("Usu�rio n�o encontrado");
			} else {
				setId(list.get(0).getIdUsuario());
				gerarNovaSenha();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String gerarHashSenha(UsuarioLogin usuarioLogin) {
		String hash = new PasswordHash().generateSaltedHash(
				usuarioLogin.getLogin(),
				usuarioLogin.getLogin(), "SHA");
		return hash;
	}
	
	public static UsuarioHome instance() {
		return ComponentUtil.getComponent("usuarioHome");
	}
	
	/**
	 * Atalho para a localiza��o atual
	 * @return a localiza��o atual do usu�rio
	 */
	public static UsuarioLocalizacao getUsuarioLocalizacaoAtual() {
		return (UsuarioLocalizacao) Contexts.getSessionContext().get(USUARIO_LOCALIZACAO_ATUAL);
	}	
}