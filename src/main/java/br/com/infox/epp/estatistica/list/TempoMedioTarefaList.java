package br.com.infox.epp.estatistica.list;

import static java.text.MessageFormat.format;

import org.jboss.seam.annotations.Name;

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
public class TempoMedioTarefaList extends AbstractPageableList<TempoMedioTarefa> {
    private static final String GROUP_BY = "group by t, ncf";

    private static final String QUERY = "select new br.com.infox.epp.estatistica.entity.TempoMedioTarefa(t, ncf, count(pet) , avg(pet.tempoGasto))"
            + " from ProcessoEpaTarefa pet"
            + " inner join pet.processoEpa p with pet.tempoGasto > 0"
            + " inner join p.naturezaCategoriaFluxo ncf"
            + " right join pet.tarefa t";

    public static final String NAME = "tempoMedioTarefaList";

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/Estatistica/tempoMedioTarefaTemplate.xls";

    private double tempoMedioProcesso;

    @Override
    protected void initCriteria() {
        addSearchCriteria("naturezaCategoriaFluxo.natureza", "(ncf.natureza=:natureza or pet is null)");
        addSearchCriteria("naturezaCategoriaFluxo.categoria", "(ncf.categoria=:categoria or pet is null)");
        addSearchCriteria("naturezaCategoriaFluxo.fluxo", "t.fluxo=:fluxo");
        addSearchCriteria("dataInicio", "cast(pet.dataInicio as timestamp) >= cast(:dataInicio as timestamp)");
        addSearchCriteria("dataFim", "cast(pet.dataFim as timestamp) >= cast(:dataFim as timestamp)");
    }

    @Override
    protected String getQuery() {
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
