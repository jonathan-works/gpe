package br.com.infox.epp.estatistica.list;

import static java.text.MessageFormat.format;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.AbstractPageableList;
import br.com.infox.epp.estatistica.entity.TempoMedioTarefa;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.tarefa.type.PrazoEnum;

/**
 * 
 * @author Erik Liberal
 * 
 */
@Name(TempoMedioTarefaList.NAME)
@Scope(ScopeType.CONVERSATION)
public class TempoMedioTarefaList extends AbstractPageableList<TempoMedioTarefa> {
    
	private static final String GROUP_BY = "group by t, ncf";

    private static final String QUERY = "select new br.com.infox.epp.estatistica.entity.TempoMedioTarefa(t, ncf, count(pt) , avg(pt.tempoGasto))"
            + " from ProcessoTarefa pt"
            + " inner join pt.processo p"
            + " inner join p.naturezaCategoriaFluxo ncf"
            + " right join pt.tarefa t ";

    public static final String NAME = "tempoMedioTarefaList";

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/Estatistica/tempoMedioTarefaTemplate.xls";

    private double tempoMedioProcesso;

    @Override
    protected void initCriteria() {
        addSearchCriteria("naturezaCategoriaFluxo.natureza", "(ncf.natureza=:natureza or pt is null)");
        addSearchCriteria("naturezaCategoriaFluxo.categoria", "(ncf.categoria=:categoria or pt is null)");
        addSearchCriteria("naturezaCategoriaFluxo.fluxo", "t.fluxo=:fluxo");
        addSearchCriteria("dataInicio", "cast(pt.dataInicio as timestamp) >= cast(:dataInicio as timestamp)");
        addSearchCriteria("dataFim", "cast(pt.dataFim as timestamp) >= cast(:dataFim as timestamp)");
        addSearchCriteria("tipoPrazo", "(not t.tipoPrazo is null and true=:tipoPrazo)");
    }

    @Override
    protected String getQuery() {
        this.getParameters().put("tipoPrazo", true);
        return QUERY;
    }

    @Override
    protected String getGroupBy() {
        return GROUP_BY;
    }

    public void exportarXLS() {
    }

    public String getTemplate() {
        return TEMPLATE;
    }

    public double getTempoMedioProcesso() {
        if (isDirty()) {
            this.tempoMedioProcesso = 0.0;
            for (final TempoMedioTarefa item : list(getResultCount())) {
                final PrazoEnum tipoPrazo = item.getTarefa().getTipoPrazo();
                final double mediaTempoGasto = item.getMediaTempoGasto();
                if (PrazoEnum.H.equals(tipoPrazo)) {
                    this.tempoMedioProcesso += (mediaTempoGasto / 1440);
                } else if (PrazoEnum.D.equals(tipoPrazo)) {
                    this.tempoMedioProcesso += mediaTempoGasto;
                }
            }
        }
        return this.tempoMedioProcesso;
    }

    public String getTempoMedioProcessoFormatado() {
        return format("{0,number,#.##} {1}", getTempoMedioProcesso(), PrazoEnum.D.getLabel());
    }

    public String getPrazoFluxo() {
        String result = "";
        final NaturezaCategoriaFluxo naturezaCategoriaFluxo = (NaturezaCategoriaFluxo) getParameters().get("naturezaCategoriaFluxo");
        if (naturezaCategoriaFluxo != null) {
            result = format("{0} {1}", naturezaCategoriaFluxo.getFluxo().getQtPrazo(), PrazoEnum.D.getLabel());
        }
        return result;
    }

}
