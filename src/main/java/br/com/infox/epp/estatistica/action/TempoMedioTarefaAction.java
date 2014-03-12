package br.com.infox.epp.estatistica.action;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.fluxo.dao.NaturezaCategoriaFluxoDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Name(TempoMedioTarefaAction.NAME)
@Scope(ScopeType.EVENT)
public class TempoMedioTarefaAction implements Serializable {

    public static final String NAME = "tempoMediaTarefaAction";
    private static final long serialVersionUID = 1L;

    @In
    private NaturezaCategoriaFluxoDAO naturezaCategoriaFluxoDAO;

    private List<Natureza> naturezaList;
    private List<Categoria> categoriaList;
    private List<Fluxo> fluxoList;
    private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList;

    @Create
    public void init() {
        // naturezaList = naturezaDAO.findAll();
        // categoriaList = categoriaDAO.findAll();
        // fluxoList = fluxoDAO.findAll();
        naturezaCategoriaFluxoList = naturezaCategoriaFluxoDAO.findAll();
    }

    public List<Natureza> getNaturezaList() {
        return naturezaList;
    }

    public void setNaturezaList(List<Natureza> naturezaList) {
        this.naturezaList = naturezaList;
    }

    public List<Categoria> getCategoriaList() {
        return categoriaList;
    }

    public void setCategoriaList(List<Categoria> categoriaList) {
        this.categoriaList = categoriaList;
    }

    public List<Fluxo> getFluxoList() {
        return fluxoList;
    }

    public void setFluxoList(List<Fluxo> fluxoList) {
        this.fluxoList = fluxoList;
    }

    public List<NaturezaCategoriaFluxo> getNaturezaCategoriaFluxoList() {
        return naturezaCategoriaFluxoList;
    }

    public void setNaturezaCategoriaFluxoList(
            List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
        this.naturezaCategoriaFluxoList = naturezaCategoriaFluxoList;
    }

    /*
     * public List<TempoMedioTarefaContainer> getTemposMediosTarefa() { final
     * HashMap<String, Object> parameters = new HashMap<>(); final StringBuilder
     * sb = new StringBuilder(); sb.append(
     * "select new TempoMedioTarefaContainer(t, ncf, count(pet), avg(pet.tempoGasto))"
     * ); sb.append(" from ProcessoEpaTarefa pet");
     * sb.append(" inner join pet.processoEpa p with pet.tempoGasto > 0");
     * sb.append(" right join pet.tarefa t");
     * sb.append(" inner join p.naturezaCategoriaFluxo ncf");
     * sb.append(" where 1=1"); if (natureza != null) { final String fieldName =
     * "natureza"; sb.append(" and ncf.natureza=:").append(fieldName);
     * parameters.put(fieldName, natureza); } if (categoria != null) { final
     * String fieldName = "categoria";
     * sb.append(" and ncf.categoria=:").append(fieldName);
     * parameters.put(fieldName, categoria); } if (fluxo != null) { final String
     * fieldName = "fluxo"; sb.append(" and ncf.fluxo=:").append(fieldName);
     * parameters.put(fieldName, fluxo); } if (dataInicio != null) { final
     * String fieldName = "dataInicio";
     * sb.append(" and cast(pet.dataInicio as timestamp) >= cast(:"
     * ).append(fieldName).append(" as timestamp)"); parameters.put(fieldName,
     * dataInicio); } if (dataFim != null) { final String fieldName = "dataFim";
     * sb
     * .append(" and cast(pet.dataFim as timestamp) <= cast(:").append(fieldName
     * ).append(" as timestamp)"); parameters.put(fieldName, dataFim); }
     * sb.append(" group by t, ncf"); return
     * genericDAO.getResultList(sb.toString(), parameters); }
     * 
     * public List<Map<String, Object>> getTemposMediosProcesso() { final String
     * hql =
     * "select new map(n as natureza, c as categoria, f as fluxo, avg(pet.tempoGasto) as mediaTempoGasto)"
     * + " from ProcessoEpaTarefa pet" +
     * " inner join pet.processoEpa p with pet.tempoGasto > 0" +
     * " right join p.naturezaCategoriaFluxo ncf" + " inner join ncf.natureza n"
     * + " inner join ncf.categoria c" + " inner join ncf.fluxo f" // +
     * " where n=:natureza" // + "  and c=:categoria" // + "  and f=:fluxo" // +
     * "  and pet.dataInicio >= :dataInicio" // +
     * "  and pet.dataFim <= :dataFim" + " group by n, c, f"; final
     * HashMap<String, Object> parameters = new HashMap<>(); //
     * parameters.put("natureza", null); // parameters.put("categoria", null);
     * // parameters.put("fluxo", null); // parameters.put("dataInicio", null);
     * // parameters.put("dataFim", null); return genericDAO.getResultList(hql,
     * parameters); }
     */
}
