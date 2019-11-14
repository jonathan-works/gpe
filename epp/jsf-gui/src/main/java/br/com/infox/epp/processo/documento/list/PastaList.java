package br.com.infox.epp.processo.documento.list;

import java.util.List;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class PastaList extends EntityList<Pasta> {

    private static final long serialVersionUID = 1L;
    private final String DEFAULT_EJBQL = "select o from Pasta o where o.processo = #{pastaList.processo}";
    private final String DEFAULT_ORDER = "ordem, o.nome"; 
    
    private Processo processo;
    
    @Override
    public List<Pasta> getResultList() {
        if (processo == null) {
            return null;
        }
        return super.getResultList();
    }
    
    @Override
    public Long getResultCount() {
        if (processo == null) {
            return 0L;
        }
        return super.getResultCount();
    }
    
    @Override
    protected void addSearchFields() {
        addSearchField("processo", SearchCriteria.IGUAL);
        addSearchField("nome", SearchCriteria.CONTENDO);
        addSearchField("codigo", SearchCriteria.CONTENDO);
        addSearchField("descricao", SearchCriteria.CONTENDO);
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

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

}
