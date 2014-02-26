package br.com.infox.epp.estatistica.action;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.tarefa.entity.Tarefa;



@Name(TempoMedioTarefaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class TempoMedioTarefaAction implements Serializable {

    public static final String NAME = "tempoMediaTarefaAction";
    private static final long serialVersionUID = 1L;
    
    @In
    private GenericDAO genericDAO;
    private Natureza natureza;
    private Categoria categoria;
    private Fluxo fluxo;
    private Date dataInicio;
    private Date dataFim;
    
    public Natureza getNatureza() {
        return natureza;
    }

    public void setNatureza(Natureza natureza) {
        this.natureza = natureza;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
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
        this.dataFim = dataFim;
    }

    public List<Map<String, Object>> getTemposMediosTarefa() {
        final HashMap<String, Object> parameters = new HashMap<>();
        final StringBuilder sb = new StringBuilder();
        /*TempoMedioTarefaNovo(Tarefa tarefa,
            NaturezaCategoriaFluxo naturezaCategoriaFluxo, Integer instancias,
            Double mediaTempoGasto)*/
        sb.append("select new TempoMedioTarefaNovo(t, ncf, count(pet), avg(pet.tempoGasto))");
        sb.append(" from ProcessoEpaTarefa pet");
        sb.append(" inner join pet.processoEpa p with pet.tempoGasto > 0");
        sb.append(" right join pet.tarefa t");
        sb.append(" inner join p.naturezaCategoriaFluxo ncf");
        sb.append(" where 1=1");
        if (natureza != null) {
            final String fieldName = "natureza";
            sb.append(" and ncf.natureza=:").append(fieldName);
            parameters.put(fieldName, natureza);
        }
        if (categoria != null) {
            final String fieldName = "categoria";
            sb.append(" and ncf.categoria=:").append(fieldName);
            parameters.put(fieldName, categoria);
        }
        if (fluxo != null) {
            final String fieldName = "fluxo";
            sb.append(" and ncf.fluxo=:").append(fieldName);
            parameters.put(fieldName, fluxo);
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
        sb.append(" group by t, ncf");
        return genericDAO.getResultList(sb.toString(), parameters);
    }
    
    public List<Map<String, Object>> getTemposMediosProcesso() {
        final String hql = "select new map(n as natureza, c as categoria, f as fluxo, avg(pet.tempoGasto) as mediaTempoGasto)"
                + " from ProcessoEpaTarefa pet"
                + " inner join pet.processoEpa p with pet.tempoGasto > 0"
                + " right join p.naturezaCategoriaFluxo ncf"
                + " inner join ncf.natureza n"
                + " inner join ncf.categoria c"
                + " inner join ncf.fluxo f"
//                + " where n=:natureza"
//                + "  and c=:categoria"
//                + "  and f=:fluxo"
//                + "  and pet.dataInicio >= :dataInicio"
//                + "  and pet.dataFim <= :dataFim"
                + " group by n, c, f";
        final HashMap<String, Object> parameters = new HashMap<>();
//        parameters.put("natureza", null);
//        parameters.put("categoria", null);
//        parameters.put("fluxo", null);
//        parameters.put("dataInicio", null);
//        parameters.put("dataFim", null);
        return genericDAO.getResultList(hql, parameters);
    }

}
