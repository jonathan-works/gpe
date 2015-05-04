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
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;

@Name(VariavelProcessoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
public class VariavelProcessoService {

    public static final String NAME = "variavelProcessoService";

    @In
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
    @In
    private MetadadoProcessoManager metadadoProcessoManager;

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
                    variaveis.add(getVariavelProcesso(processo, definicao));
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
    	DefinicaoVariavelProcesso definicao = definicaoVariavelProcessoManager.getDefinicao(processo.getNaturezaCategoriaFluxo().getFluxo(), nome);
		return getPrimeiraVariavelProcessoAncestral(processo, definicao);
    }
    
	private VariavelProcesso getPrimeiraVariavelProcessoAncestral(Processo processo, DefinicaoVariavelProcesso definicao) {
		VariavelProcesso variavelProcesso = null;
        Processo corrente = processo;
        while(corrente != null && variavelProcesso == null){
        	variavelProcesso = getVariavelProcesso(corrente, definicao);
        	corrente = corrente.getProcessoPai();
        }
		return variavelProcesso;
	}

	private VariavelProcesso getVariavelProcesso(Processo processo, DefinicaoVariavelProcesso definicao) {
		ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(processo.getIdJbpm());
        
        Object variable = processInstance.getContextInstance().getVariable(definicao.getNome());
        VariavelProcesso variavelProcesso = inicializaVariavelProcesso(processInstance, definicao);
		if (variable != null){
        	variavelProcesso= setValor(variavelProcesso, variable);
        } else {
        	List<MetadadoProcesso> metadados = metadadoProcessoManager.getMetadadoProcessoByType(processo, definicao.getNome());
        	if (metadados != null && metadados.size()>0){
        		variavelProcesso = setValor(processo, variavelProcesso, metadados);
        	} else if (definicao.getValorPadrao() != null){
        		Contexts.getEventContext().set("processo", processo);
        		variavelProcesso = setValor(definicao, variavelProcesso);
    		} else {
    			variavelProcesso = null;
    		}
        }
        return variavelProcesso;
	}

	/**
	 * Adiciona valor baseado em expressão, de valor padrão configurada na definição do fluxo, ao container de variável de processo
	 * @param definicao
	 * @param variavelProcesso
	 * @return
	 */
	private VariavelProcesso setValor(DefinicaoVariavelProcesso definicao, VariavelProcesso variavelProcesso) {
		Object result = Expressions.instance().createValueExpression(definicao.getValorPadrao()).getValue();
		variavelProcesso.setValor(result != null ? result.toString() : "");
		return variavelProcesso;
	}

	/**
	 * Adiciona valor baseado em metadado de processo ao container de variável de processo
	 * @param processo
	 * @param variavelProcesso
	 * @param metadados
	 * @return
	 */
	private VariavelProcesso setValor(Processo processo, VariavelProcesso variavelProcesso, List<MetadadoProcesso> metadados) {
		StringBuilder sb = new StringBuilder();
		boolean firstValue = true;
		for (MetadadoProcesso metadadoProcesso : metadados) {
			if (!firstValue){
				sb.append(", ");
			}
			sb.append(metadadoProcesso.getValue());
			firstValue = false;
		}
		variavelProcesso.setValor(sb.toString());
		return variavelProcesso;
	}

	/**
	 *  Adiciona valor armazenado no contexto do jbpm ao container de variável de processo
	 * @param variavelProcesso
	 * @param variable
	 * @return
	 */
	private VariavelProcesso setValor(VariavelProcesso variavelProcesso, Object variable) {
		variavelProcesso.setValor(variable.toString());
		return variavelProcesso;
	}

	private VariavelProcesso inicializaVariavelProcesso(ProcessInstance processInstance,
			DefinicaoVariavelProcesso definicao) {
		VariavelProcesso variavelProcesso = new VariavelProcesso();
		variavelProcesso.setIdProcessInstance(processInstance.getId());
		variavelProcesso.setIdToken(processInstance.getRootToken().getId());
		variavelProcesso.setLabel(definicao.getLabel());
		variavelProcesso.setNome(definicao.getNome());
		return variavelProcesso;
	}
}
