package br.com.infox.ibpm.bean.externo;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.ProcessoEpa;

@Name(ConsultaProcessoExterno.NAME)
@Scope(ScopeType.PAGE)
public class ConsultaProcessoExterno extends EntityList<ProcessoEpa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaProcessoExterno";
    
    private static final String DEFAULT_EJBQL = "select o from ProcessoEpa o";
    private static final String DEFAULT_ORDER = "dataInicio ASC";
    
    private boolean exibirTable = false;

    @Override
    protected void addSearchFields() {
        addSearchField("numeroProcesso", SearchCriteria.IGUAL);
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

}
