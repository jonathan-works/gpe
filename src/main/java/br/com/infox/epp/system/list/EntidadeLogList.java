package br.com.infox.epp.system.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.system.entity.ConsultaEntidadeLog;
import br.com.infox.epp.system.entity.EntityLog;
import br.com.infox.epp.system.entity.EntityLogDetail;
import br.com.infox.epp.system.manager.EntidadeLogManager;
import br.com.infox.epp.system.type.TipoOperacaoLogEnum;
import br.com.itx.util.EntityUtil;

@Name(EntidadeLogList.NAME)
@Scope(ScopeType.PAGE)
public class EntidadeLogList extends EntityList<EntityLog> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "entidadeLogList";
	
	@In private EntidadeLogManager entidadeLogManager;
	
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
		addSearchField("ip", SearchCriteria.INICIANDO, R1);
		addSearchField("usuario", SearchCriteria.IGUAL, R2);
		addSearchField("nomeEntidade", SearchCriteria.IGUAL, R3);
		addSearchField("tipoOperacao", SearchCriteria.IGUAL, R4);
		addSearchField("nomePackage", SearchCriteria.IGUAL, R6);
		addSearchField("idEntidade", SearchCriteria.IGUAL, R7);
		addSearchField("dataLogInicio", SearchCriteria.MAIOR_IGUAL, R8);
		addSearchField("dataLogFim", SearchCriteria.MENOR_IGUAL, R9);
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
		return entidadeLogManager.getUsuariosQuePossuemRegistrosDeLog();
	}
	
	public List<String> getNomesDasEntidades(){
		return entidadeLogManager.getEntidadesQuePodemPossuirLog();
	}
}
