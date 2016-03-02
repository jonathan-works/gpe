package br.com.infox.epp.system.list;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.system.entity.ConsultaEntidadeLog;
import br.com.infox.epp.system.entity.EntityLog;
import br.com.infox.epp.system.entity.EntityLogDetail;
import br.com.infox.epp.system.manager.EntidadeLogManager;
import br.com.infox.epp.system.type.TipoOperacaoLogEnum;

@Named
@ViewScoped
public class EntidadeLogList extends DataList<EntityLog> {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntidadeLogManager entidadeLogManager;

    private ConsultaEntidadeLog instance = new ConsultaEntidadeLog();
    private String nomePackage;
    private String idEntidade;
    private Integer idPesquisa;

    private static final String DEFAULT_EJBQL = "select o from EntityLog o ";
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
    protected void addRestrictionFields() {
    	addRestrictionField("ip", R1);
    	addRestrictionField("usuario", R2);
    	addRestrictionField("nomeEntidade", R3);
    	addRestrictionField("tipoOperacao", R4);
    	addRestrictionField("nomePackage", R6);
    	addRestrictionField("idEntidade", R7);
    	addRestrictionField("dataLogInicio", R8);
    	addRestrictionField("dataLogFim", R9);
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

    public void limparTela() {
        instance = new ConsultaEntidadeLog();
        setIdPesquisa(null);
        setIdEntidade(null);
        setNomePackage(null);
    }

    public List<EntityLogDetail> getEntityLogDetailList(Integer idEntityLog) {
        return entidadeLogManager.find(idEntityLog).getLogDetalheList();
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

    public List<UsuarioLogin> getUsuariosQuePossuemLogs() {
        return entidadeLogManager.getUsuariosQuePossuemRegistrosDeLog();
    }

    public List<String> getNomesDasEntidades() {
        return entidadeLogManager.getEntidadesQuePodemPossuirLog();
    }
}
