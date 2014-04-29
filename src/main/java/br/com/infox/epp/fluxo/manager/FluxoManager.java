package br.com.infox.epp.fluxo.manager;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.taskmgmt.def.Swimlane;
import org.xml.sax.InputSource;

import br.com.infox.constants.WarningConstants;
import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;

/**
 * Classe Manager para a entidade Fluxo
 * 
 * @author tassio
 */
@Name(FluxoManager.NAME)
@AutoCreate
public class FluxoManager extends Manager<FluxoDAO, Fluxo> {

    private static final long serialVersionUID = -6521661616139554331L;

    public static final String NAME = "fluxoManager";

    /**
     * Retorna todos os Fluxos ativos
     * 
     * @return lista de fluxos ativos
     */
    public List<Fluxo> getFluxoList() {
        return getDao().getFluxoList();
    }

    public boolean contemProcessoAtrasado(final Fluxo fluxo) {
        return getDao().quantidadeProcessosAtrasados(fluxo) > 0;
    }

    public Fluxo getFluxoByDescricao(final String descricao) {
        return getDao().getFluxoByDescricao(descricao);
    }

    public boolean existemProcessosAssociadosAFluxo(final Fluxo fluxo) {
        return getDao().getQuantidadeDeProcessoAssociadosAFluxo(fluxo) > 0;
    }

    public boolean existeFluxoComDescricao(final String descricao) {
        return getDao().existeFluxoComDescricao(descricao);
    }

    public boolean existeFluxoComCodigo(final String codigo) {
        return getDao().existeFluxoComCodigo(codigo);
    }

    @SuppressWarnings(WarningConstants.UNCHECKED)
    public Collection<Integer> getIdsLocalizacoesRaias(final Fluxo fluxo) {
        StringReader stringReader = new StringReader(fluxo.getXml());
        JpdlXmlReader jpdlReader = new JpdlXmlReader(new InputSource(stringReader));
        ProcessDefinition processDefinition = jpdlReader.readProcessDefinition();
        Map<String, Swimlane> swimlanes = processDefinition.getTaskMgmtDefinition().getSwimlanes();
        Pattern p = Pattern.compile(".+?'(.+?)'.+?");
        Set<Integer> idsLocalizacao = new HashSet<Integer>();

        for (Swimlane swimlane : swimlanes.values()) {
            String pooledActorsExpression = swimlane.getPooledActorsExpression();
            if (pooledActorsExpression == null) {
                continue;
            }
            Matcher matcher = p.matcher(swimlane.getPooledActorsExpression());
            if (!matcher.find()) {
                continue;
            }
            for (String s : matcher.group(1).split(",")) {
                String[] tokens = s.split(":");
                idsLocalizacao.add(Integer.valueOf(tokens[0]));
            }
        }

        return idsLocalizacao;
    }
}
