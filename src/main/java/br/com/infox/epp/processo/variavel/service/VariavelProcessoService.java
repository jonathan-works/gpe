package br.com.infox.epp.processo.variavel.service;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;

@Name(VariavelProcessoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
public class VariavelProcessoService {

    public static final String NAME = "variavelProcessoService";

    @In
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;

    @SuppressWarnings(UNCHECKED)
    public List<VariavelProcesso> getVariaveis(Processo processo) {
        ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(processo.getIdJbpm());
        ContextInstance contextInstance = processInstance.getContextInstance();
        List<VariavelProcesso> variaveis = new ArrayList<>();
        Map<String, Object> jbpmVariables = contextInstance.getVariables();

        for (String variableName : jbpmVariables.keySet()) {
            if (variableName != null) {
                DefinicaoVariavelProcesso definicao = definicaoVariavelProcessoManager
                        .getDefinicao(processo.getNaturezaCategoriaFluxo().getFluxo(), variableName);

                if (definicao != null && definicao.getVisivel()) {
                    variaveis.add(buildVariavelProcesso(definicao, processInstance));
                }
            }
        }

        return variaveis;
    }

    public void save(VariavelProcesso variavel) {
        ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(variavel.getIdProcessInstance());
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable(variavel.getNome(), variavel.getValor());
    }

    public VariavelProcesso getVariavelProcesso(Processo processo, String nome) {
        ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(processo.getIdJbpm());
        DefinicaoVariavelProcesso definicao = definicaoVariavelProcessoManager.getDefinicao(processo.getNaturezaCategoriaFluxo().getFluxo(), nome);
        return buildVariavelProcesso(definicao, processInstance);
    }

    private VariavelProcesso buildVariavelProcesso(
            DefinicaoVariavelProcesso definicao, ProcessInstance processInstance) {
        VariavelProcesso variavelProcesso = new VariavelProcesso();
        variavelProcesso.setIdProcessInstance(processInstance.getId());
        variavelProcesso.setIdToken(processInstance.getRootToken().getId());
        variavelProcesso.setLabel(definicao.getLabel());
        variavelProcesso.setNome(definicao.getNome());
        variavelProcesso.setValor((String) processInstance.getContextInstance().getVariable(variavelProcesso.getNome()));
        return variavelProcesso;
    }
}
