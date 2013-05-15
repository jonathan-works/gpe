package br.com.infox.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.manager.UsuarioLoginManager;
import br.com.infox.ibpm.bean.ConsultaEntidadeLog;
import br.com.infox.ibpm.entity.log.EntityLog;
import br.com.infox.ibpm.entity.log.EntityLogDetail;
import br.com.infox.type.TipoOperacaoLogEnum;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(EntidadeLogList.NAME)
@Scope(ScopeType.PAGE)
public class EntidadeLogList extends EntityList<EntityLog> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "entidadeLogList";
	
	@In private UsuarioLoginManager usuarioLoginManager;
	
	private ConsultaEntidadeLog instance = new ConsultaEntidadeLog();
	private String nomePackage;
	private String idEntidade;
	private Integer idPesquisa;
	
	private static final String DEFAULT_EJBQL = "select o from EntityLog o";
	private static final String DEFAULT_ORDER = "dataLog desc";
	
	private static final String R1 = "ip like concat(lower(#{entidadeLogList.instance.ip}),'%')";
	private static final String R2 = "usuario = #{entidadeLogList.instance.usuario}";
	private static final String R3 = "nomeEntidade = #{entidadeLogList.instance.nomeEntidade}";
	private static final String R4 = "tipoOperacao = #{entidadeLogList.instance.tipoOperacaoLogEnum}";
	private static final String R6 = "nomePackage = #{entidadeLogList.nomePackage}";
	private static final String R7 = "idEntidade = #{entidadeLogList.idEntidade}";
	private static final String R8 = "cast(dataLog as date) >= #{entidadeLogList.instance.dataInicio}";
	private static final String R9 = "cast(dataLog as date)<= #{entidadeLogList.instance.dataFim}";

	@Override
	protected void addSearchFields() {
		addSearchField("ip", SearchCriteria.iniciando, R1);
		addSearchField("usuario", SearchCriteria.igual, R2);
		addSearchField("nomeEntidade", SearchCriteria.igual, R3);
		addSearchField("tipoOperacao", SearchCriteria.igual, R4);
		addSearchField("nomePackage", SearchCriteria.igual, R6);
		addSearchField("idEntidade", SearchCriteria.igual, R7);
		addSearchField("dataLogInicio", SearchCriteria.maiorIgual, R8);
		addSearchField("dataLogFim", SearchCriteria.menorIgual, R9);
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	
	public TipoOperacaoLogEnum[] getTipoOperacaoLogEnumValues() {
		return TipoOperacaoLogEnum.values();
	}
	
	public void limparTela(){
		instance = new ConsultaEntidadeLog();
		setIdPesquisa(null);
		setIdEntidade(null);
		setNomePackage(null);
	}
	
	public List<EntityLogDetail> getEntityLogDetailList(Integer idEntityLog) {
		return EntityUtil.getEntityManager().find(EntityLog.class, idEntityLog).getLogDetalheList();
	}

	public ConsultaEntidadeLog getInstance() {
		return instance;
	}

	public void setInstance(ConsultaEntidadeLog instance) {
		this.instance = instance;
	}

	public String getNomePackage() {
		return nomePackage;
	}

	public void setNomePackage(String nomePackage) {
		this.nomePackage = nomePackage;
	}

	public String getIdEntidade() {
		return idEntidade;
	}

	public void setIdEntidade(String idEntidade) {
		this.idEntidade = idEntidade;
	}

	public Integer getIdPesquisa() {
		return idPesquisa;
	}

	public void setIdPesquisa(Integer idPesquisa) {
		this.idPesquisa = idPesquisa;
	}
	
	public List<UsuarioLogin> getUsuariosQuePossuemLogs(){
		return usuarioLoginManager.getUsuariosQuePossuemRegistrosDeLog();
	}
}
