package br.com.infox.ldap.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.list.ListPaginator;
import br.com.infox.ibpm.entity.Endereco;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.home.EnderecoHome;
import br.com.infox.ibpm.home.UsuarioHome;
import br.com.infox.ibpm.home.UsuarioLocalizacaoHome;
import br.com.infox.ldap.util.LdapUtil;
import br.com.infox.list.EnderecoList;
import br.com.infox.list.UsuarioLocList;
import br.com.infox.util.ParametroUtil;
import br.com.itx.util.ComponentUtil;

@Name(ActiveDirectoryAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ActiveDirectoryAction implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "activeDirectoryAction";
	private static final LogProvider LOG = Logging.getLogProvider(ActiveDirectoryAction.class);
	private UsuarioLogin entity = new UsuarioLogin();
	private UsuarioLogin instance = new UsuarioLogin();
	private Endereco endereco;
	private String tab;
	private ListPaginator<UsuarioLogin> usuariosADList;
	private UsuarioLocalizacao usuarioLocalizacao;
	
	public String getLDAPDomain() {
		return ParametroUtil.getLDAPDomain();
	}
	
	public String getLDAPHost() {
		return ParametroUtil.getLDAPHost();
	}
	
	public String getLDAPAuthentication() {
		return ParametroUtil.getLDAPAuthentication();
	}
	
	public String getLDAPLogin() {
		return ParametroUtil.getLDAPLogin();
	}
	
	public void importeUsuarioAD() {
		UsuarioHome home = ComponentUtil.getComponent(UsuarioHome.NAME);
		instance.setLdap(true);
		home.setInstance(instance);
		try {
			home.persist(false);
			setTab("enderecoTab");
		}catch(Exception e) {
			LogProvider log = Logging.getLogProvider(this.getClass());
			String msg = "Erro ao importar usuario do Active Directory";
			log.error(msg, e);
			FacesMessages.instance().add(Severity.ERROR, msg);
		}
	}
	
	public void incluirEndereco(){
		EnderecoHome end = EnderecoHome.instance();
		end.getInstance().setUsuario(instance);
		end.persist();
	}
	
	public void incluirUsuarioLocalizacao() {
		UsuarioLocalizacaoHome home = UsuarioLocalizacaoHome.instance();
		home.getInstance().setUsuario(instance);
		home.persist();
	}
	
	public List<Endereco> listEndereco(int elements) {
		EnderecoList enderecoList = ComponentUtil.getComponent(EnderecoList.NAME);
		enderecoList.getEntity().setUsuario(instance);
		return enderecoList.list(elements);
	}
	
	public List<UsuarioLocalizacao> listUsuarioLocalizacao(int elements) {
		UsuarioLocList usuLocList = ComponentUtil.getComponent(UsuarioLocList.NAME);
		usuLocList.getEntity().setUsuario(instance);
		return usuLocList.list(elements);
	}
	
	public void newInstance() {
		entity = new UsuarioLogin();
		entity.setAtivo(true);
		entity.setBloqueio(false);
		entity.setLdap(true);
		
		instance = new UsuarioLogin();
		instance.setAtivo(true);
		instance.setBloqueio(false);
		instance.setLdap(true);
		this.setUsuariosADList(null);
	}
	
	public void newEndereco() {
		endereco = new Endereco();
		endereco.setUsuario(instance);
		EnderecoHome end = EnderecoHome.instance();
		end.setInstance(endereco);
	}
	
	public void newUsuarioLocalizacao() {
		usuarioLocalizacao = new UsuarioLocalizacao();
		usuarioLocalizacao.setUsuario(instance);
		UsuarioLocalizacaoHome home = UsuarioLocalizacaoHome.instance();
		home.setInstance(usuarioLocalizacao);
	}
	
	public ListPaginator<UsuarioLogin> getUsuariosADList() {
		if (usuariosADList == null) {
			usuariosADList = new ListPaginator<UsuarioLogin>(listarUsuariosAD(), 20);
		}
		return usuariosADList;
	}
	
	public void setUsuariosADList(ListPaginator<UsuarioLogin> list) {
		usuariosADList = null;
	}
	
	public void removeEndereco(Endereco end) {
		EnderecoHome home = EnderecoHome.instance();
		home.remove(end);
	}
	
	public void removeUsuarioLocalizacao(UsuarioLocalizacao usu) {
		UsuarioLocalizacaoHome home = UsuarioLocalizacaoHome.instance();
		home.remove(usu);
	}
	
	public List<UsuarioLogin> listarUsuariosAD() {
		NamingEnumeration<SearchResult> results = LdapUtil.pesquisarUsuariosLDAP();
		List<UsuarioLogin> usuarios = new ArrayList<UsuarioLogin>();
		try {
			while (results.hasMore()) {
				SearchResult searchResult = results.next();
				Attributes attributes = searchResult.getAttributes();
				Attribute nome = attributes.get("displayname");
				if (nome == null) {
					continue;
				}
				Attribute log = attributes.get("userprincipalname");
				Attribute mail = attributes.get("mail");
				StringTokenizer tokens = new StringTokenizer((String) log.get(), "@");
				
				UsuarioLogin temp = preencherUsuario(nome, mail, tokens);
				if(temp != null) {
					usuarios.add(temp);
				}
			}
			results.close();
		} catch (Exception e) {
			LOG.error("[######] Erro ao preencher usuário AD [######]");
			LOG.error(".listarUsuariosAD()", e);
			LdapUtil.exibirMenssagemLDAP();
		}
		return usuarios;
	}

	private UsuarioLogin preencherUsuario(Attribute nome, Attribute mail, StringTokenizer tokens) throws NamingException {
		long time = System.currentTimeMillis();
		boolean search = false;
		boolean cadastrado = false;
		if (entity != null && entity.getNome() != null
				&& !"".equals(entity.getNome())) {
			search = true;
		}
		UsuarioLogin usu;
		usu = new UsuarioLogin();
		usu.setLogin(tokens.nextToken());

		// Verifica se o usuário já foi cadastrado
		UsuarioHome home = UsuarioHome.instance();
		UsuarioLogin user = home.checkUserByLogin(usu.getLogin());

		if (user != null && user.getLdap()) {
			cadastrado = true;
		}
		if (!cadastrado) {
			if (mail != null) {
				usu.setEmail((String) mail.get());
			}
			usu.setNome((String) nome.get());
			usu.setAtivo(true);
			usu.setBloqueio(false);
			usu.setProvisorio(false);
			usu.setSenha((time + "").hashCode() + ""
					+ (System.currentTimeMillis() + "").hashCode() + "");
		} else {
			usu = user;
		}
		
		if(search && !usu.getNome().toLowerCase().startsWith(entity.getNome().toLowerCase())) {
			usu = null;
		}
		return usu;
	}
	
	public UsuarioLogin getEntity() {
		return entity;
	}

	public void setEntity(UsuarioLogin entity) {
		this.entity = entity;
		setTab("form");
	}

	public UsuarioLogin getInstance() {
		return instance;
	}

	public void setInstance(UsuarioLogin instance) {
		this.instance = instance;
		
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}
	
	public void onClickSearchTab() {
		newInstance();
	}
	
	public void onClickFormTab() {
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public UsuarioLocalizacao getUsuarioLocalizacao() {
		return usuarioLocalizacao;
	}

	public void setUsuarioLocalizacao(UsuarioLocalizacao usuarioLocalizacao) {
		this.usuarioLocalizacao = usuarioLocalizacao;
	}

}
