/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
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
	 * Testa se os campos do bloqueio foram preenchidos corretamente
	 * Já é feita uma validação no xhtml
	 * Essa segunda validação (em código) é realmente necessária?
	 * */
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
	
	/**
	 * Apaga a data de Expiração quando o Usário passa de Provisório para Permanente 
	 * */
	private void validarPermanencia(){
		if (!getInstance().getProvisorio())
			getInstance().setDataExpiracao(null);
	}
	
	public Usuario checkUserByLogin(String login) {
		Query query = getEntityManager().createNamedQuery(UsuarioLogin.USUARIO_LOGIN_NAME);
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
		if (changed){
			ultimoBloqueio = null;
			Usuario u = getInstance();
			login = getInstance().getLogin();
			List<BloqueioUsuario> bloqueioUsuarioList = u.getBloqueioUsuarioList();
			if (!bloqueioUsuarioList.isEmpty()){
				int i = bloqueioUsuarioList.size() - 1;
				ultimoBloqueio = bloqueioUsuarioList.get(i);
			}
		}
	}
	
	/*
	 * Regra do Bloqueio
	 * 
	 * Caso 1: Bloquear usuário já Bloqueado {
	 * 			1.1: Bloqueio antigo é desfeito
	 * 			1.2: Novo bloqueio é criado
	 * 		}
	 * Caso 2: Bloquear usuário ativo
	 * 			2.1: Novo bloqueio é criado
	 * */
	@Override
	public String update() {
		validarBloqueio();
		validarPermanencia();
		Usuario usuario = getInstance();
		if (usuario.getLogin() == null) {
			usuario.setLogin(login);
		}
		if (estavaBloqueado()){
			desbloquear();
		}
		if (getInstance().getBloqueio().equals(Boolean.TRUE))
			bloquear();
		return super.update();		
	}
	
	public boolean estavaBloqueado(){
		if (ultimoBloqueio != null) {
			return (ultimoBloqueio.getDataDesbloqueio() == null);
		}
		return false;
	}
	
	public void desbloquear(){
		ultimoBloqueio.setDataDesbloqueio(new Date());
		super.update();
	}
	
	public void bloquear(){
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
	
	public String persist(boolean senha) {
		login = getInstance().getLogin();
		String resultado = super.persist();
		if (senha) {
			gerarNovaSenha();
		}		
		return resultado;
	}
	
	private void enviarEmail(String parametro)	{
		String nomeParam = "";
		if ("login".equals(parametro))	{
			nomeParam = "tituloModeloEmailMudancaSenha";
		} else if("email".equals(parametro))	{
			nomeParam = "tituloModeloEmailMudancaSenhaComLogin";
		}
		
		String objNomeModelo = ParametroHome.getParametro(nomeParam);
		
		if (objNomeModelo != null)	{
			StringBuilder sb = new StringBuilder();
			sb.append("select o from ModeloDocumento o");
			sb.append(" where o.tituloModeloDocumento = :titulo");
			
			ModeloDocumento modelo = (ModeloDocumento)getEntityManager().createQuery(sb.toString())
															.setParameter("titulo", objNomeModelo).getSingleResult();
			enviarEmailModelo(modelo);
		} else {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Erro no envio do e-mail. O parâmetro de sistema '" +
					nomeParam + "' não foi definido");
		}
	}
	
	private void enviarEmailModelo(ModeloDocumento modelo)	{
		FacesMessages fm = FacesMessages.instance();
		if (modelo != null) {
			ModeloDocumentoAction action = (ModeloDocumentoAction) Component.getInstance(ModeloDocumentoAction.NAME); 
			String body = action.getConteudo(modelo);

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
			fm.add(StatusMessage.Severity.ERROR, "Erro no envio do e-mail. O parâmetro de sistema " +
			"'idModeloEMailMudancaSenha' está definido com valor inválido");				
		}
	}
	
	//TODO Mandar e-mail com login - quando o usuário esquece o login não há meio de recuperá-lo
	private void enviarEmail() {
		//obter o id do modelo do contexto da aplicação
		Object objIdModelo = Contexts.getApplicationContext().get("idModeloEMailMudancaSenha");
		if (objIdModelo != null) {
			String idModelo = objIdModelo.toString(); 
			ModeloDocumento modelo = getEntityManager().find(ModeloDocumento.class, new Integer(idModelo));
			
			enviarEmailModelo(modelo);
			
		} else {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Erro no envio do e-mail. O parâmetro de sistema " +
					"'idModeloEMailMudancaSenha' não foi definido");
		}
	}
	
	public void gerarNovaSenha(String parametro)	{
		password = RandomStringUtils.randomAlphabetic(8); 
		new RunAsOperation(true) {
			@Override
			public void execute() {
				IdentityManager.instance().changePassword(login, password);
			}
		}.run();
		getInstance().setProvisorio(true);
		EntityManager em = EntityUtil.getEntityManager();
		em.merge(getInstance());
		em.flush();
		if (parametro == null)	{
			enviarEmail();
		} else {
			enviarEmail(parametro);
		}
	}
	
	public void gerarNovaSenha() {
		gerarNovaSenha(null);
	}
	
	public void gerarSenhaInicial() {
		new RunAsOperation(true) {
			@Override
			public void execute() {
				IdentityManager.instance().changePassword(getInstance().getLogin(), getInstance().getLogin());
			}
		}.run();
	}
	
	/**
	 * Metodo que gera uma nova senha para usuário. Este metodo faz isso buscando 
	 * na base do wiacs o usuário pelo login e email e retorna uma mensagem de erro caso
	 * não encontre. A partir do usuário do wiacs é dado um setId utilizando a 'identificacao'.
	 * @throws LoginException 
	 */
	public void requisitarNovaSenha() throws LoginException {
		
		FacesMessages fm = FacesMessages.instance();
		
		if (email.isEmpty() && login.isEmpty()) {
			fm.add("É preciso informar o login ou o e-mail do usuário");
		}
		else if (!login.isEmpty()){
			recoverBy("login", login);
		}else if (!email.isEmpty()){
			recoverBy("email", email);
		}
	}
	
	private void recoverBy(String parametro, String valor){
		FacesMessages fm = FacesMessages.instance();
		
		//O StringBuilder constrói a Query com base no parametro passado 
		// deixando na forma "select o from Usuario o where o.parametro = :parametro"
		StringBuilder sb = new StringBuilder();
		sb.append("select o from Usuario o where o.");
		sb.append(parametro);
		sb.append(" = :");
		sb.append(parametro);
		Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter(parametro, valor);
		Usuario usuario = (Usuario) query.getSingleResult();
		if (usuario == null){
			fm.add("Usuário não encontrado");
		} else{
			setId(usuario.getIdUsuario());
			gerarNovaSenha(parametro);
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
	 * Atalho para a localização atual
	 * @return a localização atual do usuário
	 */
	public static UsuarioLocalizacao getUsuarioLocalizacaoAtual() {
		return (UsuarioLocalizacao) Contexts.getSessionContext().get(USUARIO_LOCALIZACAO_ATUAL);
	}
		
	//----Getters e Setters----
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