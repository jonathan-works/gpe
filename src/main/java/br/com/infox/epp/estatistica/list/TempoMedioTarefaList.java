package br.com.infox.epp.estatistica.list;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.list.Pageable;
import br.com.infox.epp.estatistica.entity.TempoMedioTarefa;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.tarefa.type.PrazoEnum;

@Name(TempoMedioTarefaList.NAME)
@Scope(ScopeType.CONVERSATION)
public class TempoMedioTarefaList implements Serializable, Pageable {
	public static final String NAME = "tempoMedioTarefaList";
	
	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/Estatistica/tempoMedioTarefaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "relatorioTemposMedios.xls";

    @In
    private GenericDAO genericDAO;
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private Date dataInicio;
	private Date dataFim;
	
	private boolean isDirty=false;
	private Integer maxAmmount;
	private Integer page;
	private Integer pageCount;
	private List<TempoMedioTarefa> resultList;
	private double tempoMedioProcesso;
	
	public List<TempoMedioTarefa> list() {
	    return list(15);
	}
	
	private void getResultList() {
	    if (isDirty) {
            final StringBuilder sb = new StringBuilder();
            sb.append("select new br.com.infox.epp.estatistica.entity.TempoMedioTarefa(t, ncf, count(pet) , avg(pet.tempoGasto))");
            final HashMap<String, Object> parameters = getDynamicQuery(sb);
            sb.append(" group by t, ncf");
            this.resultList = genericDAO.getResultList(sb.toString(), parameters);
            isDirty = false;
        }
	}
	
	public List<TempoMedioTarefa> list(int maxAmmount) {
        this.maxAmmount = maxAmmount;
	    getResultList();
        return truncList();
	}
	
	public List<TempoMedioTarefa> truncList() {
	    final int fromIndex = (page-1)*maxAmmount;
	    final int toIndex = maxAmmount*page;
        final int listSize = this.resultList.size();
	    return resultList.subList(fromIndex, toIndex > listSize ? listSize : toIndex);
	}

    private HashMap<String, Object> getDynamicQuery(final StringBuilder sb) {
        sb.append(" from ProcessoEpaTarefa pet");
        sb.append(" inner join pet.processoEpa p with pet.tempoGasto > 0");
        sb.append(" inner join p.naturezaCategoriaFluxo ncf");
        sb.append(" right join pet.tarefa t");
        sb.append(" where 1=1");

        final HashMap<String, Object> parameters = new HashMap<>();
        if (naturezaCategoriaFluxo != null) {
            final String fieldName = "fluxo";
            parameters.put(fieldName, naturezaCategoriaFluxo.getFluxo());
            parameters.put("natureza", naturezaCategoriaFluxo.getNatureza());
            parameters.put("categoria", naturezaCategoriaFluxo.getCategoria());
            sb.append(" and t.fluxo=:fluxo");
            sb.append(" and (ncf.natureza=:natureza or pet is null)");
            sb.append(" and (ncf.categoria=:categoria or pet is null)");
        }
        if (dataInicio != null) {
            final String fieldName = "dataInicio";
            sb.append(" and cast(pet.dataInicio as timestamp) >= cast(:").append(fieldName).append(" as timestamp)");
            parameters.put(fieldName, dataInicio);
        }
        if (dataFim != null) {
            final String fieldName = "dataFim";
            sb.append(" and cast(pet.dataFim as timestamp) <= cast(:").append(fieldName).append(" as timestamp)");
            parameters.put(fieldName, dataFim);
        }
        
        return parameters;
    }

    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
        return naturezaCategoriaFluxo;
    }

    public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
        isDirty = isDirty || !areEqual(this.naturezaCategoriaFluxo, naturezaCategoriaFluxo);
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
    }

    public boolean areEqual(Object obj1, Object obj2) {
        return obj1==obj2 || obj1 != null && obj1.equals(obj2);
    }
    
    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        if (dataInicio != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(dataInicio);
            c.set(GregorianCalendar.HOUR_OF_DAY, 0);
            c.set(GregorianCalendar.MINUTE, 0);
            c.set(GregorianCalendar.SECOND, 0);
            c.set(GregorianCalendar.MILLISECOND, 0);
            dataInicio = c.getTime();
        } 
        isDirty = isDirty || !areEqual(this.dataInicio, dataInicio);
        this.dataInicio = dataInicio;            
        
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        if (dataFim != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(dataFim);
            c.set(GregorianCalendar.HOUR_OF_DAY, 23);
            c.set(GregorianCalendar.MINUTE, 59);
            c.set(GregorianCalendar.SECOND, 59);
            c.set(GregorianCalendar.MILLISECOND, 999);
            dataFim = c.getTime();
        }
        isDirty = isDirty || !areEqual(this.dataFim, dataFim);
        this.dataFim = dataFim;
    }
    
    public void newInstance() {
        naturezaCategoriaFluxo = null;
        dataInicio = null;
        dataFim = null;
    }

    @Override
    public void setPage(final Integer page) {
        this.page = page;
    }

    @Override
    public boolean isPreviousExists() {
        return getPage() > 1;
    }

    @Override
    public boolean isNextExists() {
        return getPage() < getPageCount();
    }

    @Override
    public Integer getPage() {
        if (page == null || page < 0) {
            page = 1;
        }
        final Integer count = getPageCount();
        if (page > count) {
            page = count;
        }
        return page;
    }

    @Override
    public Integer getPageCount() {
        if (pageCount == null || isDirty) {
            final int size = resultList.size();
            final int estimatedPageCount = size / maxAmmount;
            if (size % maxAmmount == 0) {
                pageCount = estimatedPageCount;
            } else {
                pageCount = estimatedPageCount+1;
            }
        }
        return pageCount;
    }
    
    public Integer getResultCount() {
        return resultList.size();
    }

    public void exportarXLS() {
    }
    
    public String getTemplate() {
        return TEMPLATE;
    }

    public double getTempoMedioProcesso() {
        if (isDirty) {
            getResultList();
            this.tempoMedioProcesso=0.0;
            for (TempoMedioTarefa item : resultList) {
                final PrazoEnum tipoPrazo = item.getTarefa().getTipoPrazo();
                if (PrazoEnum.H.equals(tipoPrazo)) {
                    this.tempoMedioProcesso+= item.getMediaTempoGasto() / (1440);
                } else if (PrazoEnum.D.equals(tipoPrazo)) {
                    this.tempoMedioProcesso+= item.getMediaTempoGasto();
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
