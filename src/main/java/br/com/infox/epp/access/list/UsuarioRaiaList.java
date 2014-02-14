package br.com.infox.epp.access.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
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
	
	private static final String DEFAULT_EJBQL = "select distinct u from UsuarioLocalizacao ul "
			+ "inner join ul.usuario u "
			+ "where u.pessoaFisica is not null "
			+ "and ul.localizacao in #{usuarioRaiaList.localizacoes}";
	
	private static final String DEFAULT_ORDER = "u.nomeUsuario";

	private static final String R1 = "ul.localizacao = #{usuarioRaiaList.localizacao}";
	private static final String R2 = "lower(u.nomeUsuario) like concat('%', lower(#{usuarioRaiaList.entity.nomeUsuario}), '%')";
	
	@In
	private LocalizacaoManager localizacaoManager;
	
	@In
	private FluxoManager fluxoManager;
	
	private List<Localizacao> localizacoes;
	private Localizacao localizacao;
	
	@Override
	protected void addSearchFields() {
		addSearchField("ul.localizacao", SearchCriteria.IGUAL, R1);
		addSearchField("u.nomeUsuario", SearchCriteria.CONTENDO, R2);
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
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
	}
	
	public void clear() {
		this.localizacao = null;
		getEntity().setNomeUsuario(null);
	}
}
