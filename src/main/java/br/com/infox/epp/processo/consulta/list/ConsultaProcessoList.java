package br.com.infox.epp.processo.consulta.list;

import java.util.Date;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.MetadadoProcessoType;

@Name(ConsultaProcessoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class ConsultaProcessoList extends EntityList<Processo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaProcessoList";

    private static final String DEFAULT_EJBQL = "select o from Processo o";
    private static final String DEFAULT_ORDER = "o.prioridadeProcesso, o.dataInicio";

    private static final String R1 = "o.idProcesso in (#{painelUsuarioHome.processoIdList})";
    private static final String R2 = "o.caixa.idCaixa = #{painelUsuarioHome.idCaixa}";
    private static final String R3 = "o.numeroProcesso = #{consultaProcessoList.entity.numeroProcesso}";
    private static final String R4 = "o.naturezaCategoriaFluxo.natureza = #{consultaProcessoList.entity.naturezaCategoriaFluxo.natureza}";
    private static final String R5 = "o.naturezaCategoriaFluxo.categoria = #{consultaProcessoList.entity.naturezaCategoriaFluxo.categoria}";
    private static final String R6 = "cast(o.dataInicio as date) >= #{consultaProcessoList.dataInicio}";
    private static final String R7 = "cast(o.dataInicio as date) <= #{consultaProcessoList.dataFim}";
    
    private static final String R8 = "exists (select 1 from MetadadoProcesso mp where mp.processo = o and mp.metadadoType = " 
    									+ MetadadoProcessoType.UNIDADE_DECISORA_MONOCRATICA 
    									+ " and cast(mp.valor, integer) = #{authenticator.monocraticaLogada.idUnidadeDecisoraMonocratica})";
    
    private static final String R9 = "exists (select 1 from MetadadoProcesso mp where mp.processo = o and mp.metadadoType = " 
										+ MetadadoProcessoType.UNIDADE_DECISORA_COLEGIADA 
										+ " and cast(mp.valor, integer) = #{authenticator.colegiadaLogada.idUnidadeDecisoraColegiada})";
    		
    private Date dataInicio;
    private Date dataFim;

    @Override
    public void newInstance() {
        super.newInstance();
        getEntity().setNaturezaCategoriaFluxo(new NaturezaCategoriaFluxo());
        setDataInicio(null);
        setDataFim(null);
    }

    @Override
    protected void addSearchFields() {
        addSearchField("idProcesso", SearchCriteria.IGUAL, R1);
        addSearchField("caixa.idCaixa", SearchCriteria.IGUAL, R2);
        addSearchField("numeroProcesso", SearchCriteria.IGUAL, R3);
        addSearchField("naturezaCategoriaFluxo.natureza", SearchCriteria.IGUAL, R4);
        addSearchField("naturezaCategoriaFluxo.categoria", SearchCriteria.IGUAL, R5);
        addSearchField("dataInicio", SearchCriteria.MAIOR_IGUAL, R6);
        addSearchField("dataFim", SearchCriteria.MENOR_IGUAL, R7);
        addSearchField("decisoraMonocratica", SearchCriteria.IGUAL, R8);
        addSearchField("decisoraColegiada", SearchCriteria.IGUAL, R9);
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

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = br.com.infox.core.util.DateUtil.getEndOfDay(dataFim);
    }
    
    public Item getItemDoProcesso(Processo processo){
    	MetadadoProcesso metadado = processo.getMetadado(MetadadoProcessoType.ITEM_DO_PROCESSO);
    	return  metadado != null ? (Item) metadado.getValue() : null;
    }

}
