package br.com.infox.epp.estatistica.list;

import static java.text.MessageFormat.format;

import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.list.AbstractPageableList;
import br.com.infox.epp.estatistica.entity.TempoMedioTarefa;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.tarefa.type.PrazoEnum;

@Name(TempoMedioTarefaList.NAME)
public class TempoMedioTarefaList extends AbstractPageableList<TempoMedioTarefa> {
	private static final String FIELD_DATA_FIM = "dataFim";
    private static final String FIELD_DATA_INICIO = "dataInicio";
    private static final String FIELD_CATEGORIA = "categoria";
    private static final String FIELD_NATUREZA = "natureza";
    private static final String FIELD_FLUXO = "fluxo";

    public static final String NAME = "tempoMedioTarefaList";
	
	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/Estatistica/tempoMedioTarefaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "relatorioTemposMedios.xls";

	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private Date dataInicio;
	private Date dataFim;
	private double tempoMedioProcesso;
	
	@Override
	protected String getQuery() {
	    final StringBuilder sb = new StringBuilder();
        sb.append("select new br.com.infox.epp.estatistica.entity.TempoMedioTarefa(t, ncf, count(pet) , avg(pet.tempoGasto))");
        sb.append(" from ProcessoEpaTarefa pet");
        sb.append(" inner join pet.processoEpa p with pet.tempoGasto > 0");
        sb.append(" inner join p.naturezaCategoriaFluxo ncf");
        sb.append(" right join pet.tarefa t");
        sb.append(" where 1=1");
        
        if (naturezaCategoriaFluxo != null) {
            sb.append(" and t.fluxo=:");
            sb.append(FIELD_FLUXO);
            sb.append(" and (ncf.natureza=:");
            sb.append(FIELD_NATUREZA);
            sb.append(" or pet is null)");
            sb.append(" and (ncf.categoria=:");
            sb.append(FIELD_CATEGORIA);
            sb.append(" or pet is null)");
        }
        
        if (dataInicio != null) {
            sb.append(" and cast(pet.dataInicio as timestamp) >= cast(:");
            sb.append(FIELD_DATA_INICIO);
            sb.append(" as timestamp)");
        }
        if (dataFim != null) {
            sb.append(" and cast(pet.dataFim as timestamp) <= cast(:");
            sb.append(FIELD_DATA_FIM);
            sb.append(" as timestamp)");
        }
        sb.append(" group by t, ncf");
        return sb.toString();
	}
	
    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
        return naturezaCategoriaFluxo;
    }

    public void setNaturezaCategoriaFluxo(final NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
        if (!areEqual(this.naturezaCategoriaFluxo, naturezaCategoriaFluxo)) {
            this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
            addParameter(FIELD_FLUXO, naturezaCategoriaFluxo.getFluxo());
            addParameter(FIELD_NATUREZA, naturezaCategoriaFluxo.getNatureza());
            addParameter(FIELD_CATEGORIA, naturezaCategoriaFluxo.getCategoria());
        }
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        if (dataInicio != null) {
            final GregorianCalendar c = new GregorianCalendar();
            c.setTime(dataInicio);
            c.set(GregorianCalendar.HOUR_OF_DAY, 0);
            c.set(GregorianCalendar.MINUTE, 0);
            c.set(GregorianCalendar.SECOND, 0);
            c.set(GregorianCalendar.MILLISECOND, 0);
            dataInicio = c.getTime();
        } 
        if (!areEqual(this.dataInicio, dataInicio)) {
            this.dataInicio = dataInicio;
            addParameter(FIELD_DATA_INICIO, dataInicio);
        }            
        
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        if (dataFim != null) {
            final GregorianCalendar c = new GregorianCalendar();
            c.setTime(dataFim);
            c.set(GregorianCalendar.HOUR_OF_DAY, 23);
            c.set(GregorianCalendar.MINUTE, 59);
            c.set(GregorianCalendar.SECOND, 59);
            c.set(GregorianCalendar.MILLISECOND, 999);
            dataFim = c.getTime();
        }
        if (!areEqual(this.dataFim, dataFim)) {
            addParameter(FIELD_DATA_FIM, dataFim);
            this.dataFim = dataFim;
        }
    }
    
    @Override
    public void newInstance() {
        naturezaCategoriaFluxo = null;
        dataInicio = null;
        dataFim = null;
        clearParameters();
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
        if (naturezaCategoriaFluxo != null) {
            result = format("{0} {1}", naturezaCategoriaFluxo.getFluxo().getQtPrazo(), PrazoEnum.D.getLabel());
        }
        return result;
    }

}