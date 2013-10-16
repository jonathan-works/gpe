package br.com.infox.ibpm.bean.externo;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.ProcessoEpa;

@Name(ConsultaProcessoExternoPorPartes.NAME)
@Scope(ScopeType.PAGE)
public class ConsultaProcessoExternoPorPartes extends EntityList<ProcessoEpa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaProcessoExternoPorPartes";
    
    private static final String DEFAULT_EJBQL = "select o from ProcessoEpa o inner join o.naturezaCategoriaFluxo ncf inner join ncf.natureza n where n.hasPartes = true";
    private static final String DEFAULT_ORDER = "o.dataInicio ASC";
    
    private String nomePartes;
    
    private static final String R1 = "exists (select p from ParteProcesso pp inner join pp.pessoa p " +
    		"where lower(p.nome) like lower(concat('%', #{consultaProcessoExternoPorPartes.nomePartes}, '%'))" +
    		" and pp.processo = o)";
    
    private boolean exibirTable = false;

    @Override
    protected void addSearchFields() {
        addSearchField("nome", SearchCriteria.CONTENDO, R1);

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
        // TODO Auto-generated method stub
        return null;
    }
    
    public void exibirTable(){
        exibirTable = true;
    }
    
    public void esconderTable(){
        newInstance();
        exibirTable = false;
    }
    
    public boolean isExibirTable(){
        return this.exibirTable;
    }

    public String getNomePartes() {
        return nomePartes;
    }

    public void setNomePartes(String nomePartes) {
        this.nomePartes = nomePartes;
    }

}
