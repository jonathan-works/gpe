package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;

@Name(ExpedicaoComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
public class ExpedicaoComunicacaoList extends EntityList<ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "expedicaoComunicacaoList";
	
	private static final String DEFAULT_EJBQL = "select o from ModeloComunicacao o where o.finalizada = true and "
			+ "o.localizacaoResponsavelAssinatura = #{authenticator.getUsuarioPerfilAtual().localizacao} and "
			+ "(o.perfilResponsavelAssinatura is null or o.perfilResponsavelAssinatura = #{authenticator.getUsuarioPerfilAtual().perfilTemplate})";
	
	private static final String DEFAULT_ORDER = "id";
	private static final String R1 = " exists (select 1 from DestinatarioModeloComunicacao d where d.modeloComunicacao = o "
			+ "and d.expedido = false) ";
	private static final String R2 = " o.processo.numeroProcesso = #{expedicaoComunicacaoList.numeroProcesso} ";
	private static final String R3 = " o.tipoComunicacao = #{expedicaoComunicacaoList.tipoComunicacao} ";

	private Boolean expedida;
	private String numeroProcesso;
	private TipoComunicacao tipoComunicacao;
	
	@Override
	protected void addSearchFields() {
		addSearchField("numeroProcesso", SearchCriteria.IGUAL, R2);
		addSearchField("tipoComunicacao", SearchCriteria.IGUAL, R3);
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
	
	public Boolean getExpedida() {
		return expedida;
	}
	
	public void setExpedida(Boolean expedida) {
		this.expedida = expedida;
		if (expedida == null) {
			setEjbql(getDefaultEjbql());
		} else if (expedida) {
			setEjbql(getDefaultEjbql() + " and not " + R1);
		} else {
			setEjbql(getDefaultEjbql() + " and " + R1);
		}
		setRestrictions();
	}
	
	public String getNumeroProcesso() {
		return numeroProcesso;
	}
	
	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}
	
	public TipoComunicacao getTipoComunicacao() {
		return tipoComunicacao;
	}
	
	public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		setNumeroProcesso(null);
		setTipoComunicacao(null);
		setExpedida(null);
	}
}
