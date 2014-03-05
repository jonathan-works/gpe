package br.com.infox.epp.access.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;

@Name(UsuarioRaiaList.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class UsuarioRaiaList extends EntityList<UsuarioLogin> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioRaiaList";
	
	private static final String DEFAULT_EJBQL = "select u.* from tb_usuario_login u "
			+ "where u.id_pessoa_fisica is not null "
			+ "and exists (select 1 from tb_usuario_localizacao ul where ul.id_usuario = u.id_usuario_login ";
	
	private static final String DEFAULT_ORDER = "u.nm_usuario";

	@In
	private LocalizacaoManager localizacaoManager;
	
	@In
	private FluxoManager fluxoManager;
	
	private List<Localizacao> localizacoes;
	private Localizacao localizacao;
	
	public UsuarioRaiaList() {
		setNativeQuery(true);
		setResultClass(UsuarioLogin.class);
	}
	
	@Override
	protected void addSearchFields() {
	}

	@Override
	protected String getDefaultEjbql() {
		StringBuilder sb = new StringBuilder(DEFAULT_EJBQL);
		if (localizacoes != null) {
			sb.append("and ul.id_localizacao in (");
			for (Localizacao localizacao : localizacoes) {
				sb.append(localizacao.getIdLocalizacao());
				sb.append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("))");
		}
		return sb.toString();
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	public List<Localizacao> getLocalizacoes() {
		return localizacoes;
	}
	
	public Localizacao getLocalizacao() {
		return localizacao;
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	public void loadLocalizacoes(Fluxo fluxo) {
		this.localizacoes = localizacaoManager.getLocalizacoes(fluxoManager.getIdsLocalizacoesRaias(fluxo));
		refreshQuery();
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		this.localizacao = null;
		refreshQuery();
	}
	
	public void refreshQuery() {
		StringBuilder sb = new StringBuilder();
		
		if (getLocalizacao() != null) {
			sb.append(DEFAULT_EJBQL);
			sb.append(" and ul.id_localizacao = ");
			sb.append(getLocalizacao().getIdLocalizacao());
			sb.append(") ");
		} else {
			sb.append(getDefaultEjbql());
		}
		
		if (getEntity().getNomeUsuario() != null) {
			sb.append(" and u.nm_usuario ilike '%");
			sb.append(getEntity().getNomeUsuario());
			sb.append("%'");
		}
		setEjbql(sb.toString());
	}
}