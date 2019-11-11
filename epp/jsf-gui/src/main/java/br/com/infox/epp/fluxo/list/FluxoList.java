package br.com.infox.epp.fluxo.list;

import java.util.List;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Named
@ViewScoped
public class FluxoList extends EntityList<Fluxo> {
    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_EJBQL = "select o from Fluxo o";
    private static final String DEFAULT_ORDER = "fluxo";
    
    private static final String TEMPLATE = "/Fluxo/fluxoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Fluxos.xls";

    @Override
    protected void addSearchFields() {
        addSearchField("codFluxo", SearchCriteria.CONTENDO);
        addSearchField("fluxo", SearchCriteria.CONTENDO);
        addSearchField("publicado", SearchCriteria.IGUAL);
        addSearchField("ativo", SearchCriteria.IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        return FluxoList.DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return FluxoList.DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    @Override
    public List<Fluxo> getResultList() {
        setEjbql(getEjbqlRestrictedWithDataPublicacao());
        return super.getResultList();
    }

    private String getEjbqlRestrictedWithDataPublicacao() {
        if (getEntity().getDataInicioPublicacao() == null
                && getEntity().getDataFimPublicacao() == null) {
            return getDefaultEjbql();
        }

        StringBuilder sb = new StringBuilder(getDefaultEjbql());
        sb.append(" where ");

        if (getEntity().getDataInicioPublicacao() != null
                && getEntity().getDataFimPublicacao() != null) {
            sb.append("o.dataInicioPublicacao >= #{fluxoList.entity.dataInicioPublicacao}");
            sb.append(" and o.dataFimPublicacao <= #{fluxoList.entity.dataFimPublicacao}");
        } else if (getEntity().getDataInicioPublicacao() != null) {
            sb.append("o.dataInicioPublicacao >= #{fluxoList.entity.dataInicioPublicacao}");
        } else {
            sb.append("o.dataFimPublicacao <= #{fluxoList.entity.dataFimPublicacao}");
        }

        return sb.toString();
    }
    
    @Override
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
}
