package br.com.infox.epp.processo.consulta.list;

import java.util.Date;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;

@AutoCreate
@Scope(ScopeType.PAGE)
@Name(ConsultaProcessoList.NAME)
public class ConsultaProcessoList extends EntityList<Processo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaProcessoList";

    private static final String DEFAULT_EJBQL = "select o from Processo o";
    private static final String DEFAULT_ORDER = "o.prioridadeProcesso, o.dataInicio";

    private static final String R1 = "o.idProcesso in (#{painelUsuarioController.processoIdList})";
    private static final String R2 = "o.caixa.idCaixa = #{painelUsuarioController.idCaixa}";
    
    @Override
    public void newInstance() {
        super.newInstance();
        getEntity().setNaturezaCategoriaFluxo(new NaturezaCategoriaFluxo());
    }

    @Override
    protected void addSearchFields() {
        addSearchField("idProcesso", SearchCriteria.IGUAL, R1);
        addSearchField("caixa.idCaixa", SearchCriteria.IGUAL, R2);
        addSearchField("numeroProcesso", SearchCriteria.IGUAL);
        addSearchField("naturezaCategoriaFluxo.natureza", SearchCriteria.IGUAL);
        addSearchField("naturezaCategoriaFluxo.categoria", SearchCriteria.IGUAL);
        addSearchField("dataInicio", SearchCriteria.DATA_MAIOR_IGUAL);
        addSearchField("dataFim", SearchCriteria.DATA_MENOR_IGUAL);
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

    public Date getDataFim() {
        return getEntity().getDataFim();
    }

    public void setDataFim(Date dataFim) {
        getEntity().setDataFim(DateUtil.getEndOfDay(dataFim));
    }
    
    public Item getItemDoProcesso(Processo processo){
    	MetadadoProcesso metadado = processo.getMetadado(EppMetadadoProvider.ITEM_DO_PROCESSO);
    	return  metadado != null ? (Item) metadado.getValue() : null;
    }

}
