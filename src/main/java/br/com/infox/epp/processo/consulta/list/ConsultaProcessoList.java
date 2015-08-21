package br.com.infox.epp.processo.consulta.list;

import java.util.Date;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
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
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;

@AutoCreate
@Scope(ScopeType.PAGE)
@Name(ConsultaProcessoList.NAME)
public class ConsultaProcessoList extends EntityList<Processo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaProcessoList";

    private static final String DEFAULT_EJBQL = "select o from Processo o";
    private static final String DEFAULT_ORDER = "coalesce(o.prioridadeProcesso, -1) DESC, o.dataInicio ASC";

    private static final String R1 = "o.idProcesso in (#{painelUsuarioController.processoIdList})";
    private static final String R2 = "o.caixa.idCaixa = #{painelUsuarioController.idCaixa}";
    private static final String R3 = "NumeroProcessoRoot(o.idProcesso) = #{consultaProcessoList.numeroProcessoRoot}";
    private static final String R4 = " ( select nat.natureza "
    		+ " from Processo pc "
    		+ " inner join pc.naturezaCategoriaFluxo nat "
    		+ " where  pc.idProcesso = o.processoRoot.idProcesso ) = #{consultaProcessoList.entity.naturezaCategoriaFluxo.natureza}";
    private static final String R5 = " ( select nat.categoria "
    		+ " from Processo pc "
    		+ " inner join pc.naturezaCategoriaFluxo nat "
    		+ " where pc.idProcesso = o.processoRoot.idProcesso ) = #{consultaProcessoList.entity.naturezaCategoriaFluxo.categoria}";
    
    @In
    private ProcessoTarefaManager processoTarefaManager;
    
    private String numeroProcessoRoot;
    
    @Override
    public void newInstance() {
        super.newInstance();
        getEntity().setNaturezaCategoriaFluxo(new NaturezaCategoriaFluxo());
        setNumeroProcessoRoot(null);
    }

    @Override
    protected void addSearchFields() {
        addSearchField("idProcesso", SearchCriteria.IGUAL, R1);
        addSearchField("caixa.idCaixa", SearchCriteria.IGUAL, R2);
        addSearchField("numeroProcesso", SearchCriteria.IGUAL);
        addSearchField("numeroProcessoRoot", SearchCriteria.IGUAL, R3);
        addSearchField("naturezaCategoriaFluxo.natureza", SearchCriteria.IGUAL,R4);
        addSearchField("naturezaCategoriaFluxo.categoria", SearchCriteria.IGUAL,R5);
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

    public Date getDataEntradaUltimaTarefa(Processo processo) {
        ProcessoTarefa pt = processoTarefaManager.getUltimoProcessoTarefa(processo);
        return pt.getDataInicio();
    }

    public String getNumeroProcessoRoot() {
        return numeroProcessoRoot;
    }

    public void setNumeroProcessoRoot(String numeroProcessoRoot) {
        this.numeroProcessoRoot = numeroProcessoRoot;
    }
}
