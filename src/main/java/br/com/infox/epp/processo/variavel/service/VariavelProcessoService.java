package br.com.infox.epp.processo.variavel.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.MethodExpression;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.google.common.base.Strings;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoSearch;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.seam.exception.BusinessException;

@Stateless
@Name(VariavelProcessoService.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
@Transactional
public class VariavelProcessoService {

    public static final String NAME = "variavelProcessoService";

    @Inject
    private ProcessoManager processoManager;
    @Inject
    private ProcessoTarefaManager processoTarefaManager;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;

    public List<VariavelProcesso> getVariaveis(Processo processo, String recursoVariavel, boolean usuarioExterno) {
        List<VariavelProcesso> variaveis = new ArrayList<>();
        Fluxo fluxo = processo.getNaturezaCategoriaFluxo().getFluxo();
        List<DefinicaoVariavelProcesso> definicaoVariavelList = definicaoVariavelProcessoSearch.getDefinicoesVariaveis(fluxo, recursoVariavel, usuarioExterno);
        
        for (DefinicaoVariavelProcesso definicao : definicaoVariavelList) {
        	variaveis.add(getPrimeiraVariavelProcessoAncestral(processo, definicao, null));
        }

        return variaveis;
    }
    
    public VariavelProcesso getVariavelProcesso(Integer idProcesso, String nome) {
    	Processo processo =  processoManager.find(idProcesso);
    	ProcessoTarefa processoTarefa = processoTarefaManager.getUltimoProcessoTarefa(processo);
    	return getVariavelProcesso(processo, nome, processoTarefa.getTaskInstance());
    }
    
    public VariavelProcesso getVariavelProcesso(Integer idProcesso, String nome, Long idTaskInstance) {
        Processo processo = processoManager.find(idProcesso);
        return getVariavelProcesso(processo, nome, idTaskInstance);
    }

    public VariavelProcesso getVariavelProcesso(Processo processo, String nome, Long idTaskInstance) {
    	DefinicaoVariavelProcesso definicao = null;
    	definicao = definicaoVariavelProcessoSearch.getDefinicao(processo.getNaturezaCategoriaFluxo().getFluxo(), nome);
    	if(definicao == null && idTaskInstance != null){
    		//TODO: melhorar código pois foi feito rapidamente...
    		//caso não encontre a definição no processo, procura na definicao do subprocesso.
    		ProcessInstance subrProcessInstance = processoManager.findProcessByTaskInstance(idTaskInstance);
    		Fluxo subFluxo = fluxoManager.getFluxoByDescricao(subrProcessInstance.getProcessDefinition().getName());
   			definicao = definicaoVariavelProcessoSearch.getDefinicao(subFluxo, nome);
    	}
    	if(definicao == null)
    		throw new BusinessException("Não foi possível encontrar a definição da variável " + nome);
    	
        TaskInstance taskInstance = idTaskInstance != null ? ManagedJbpmContext.instance().getTaskInstance(idTaskInstance) : null;
        return getPrimeiraVariavelProcessoAncestral(processo, definicao, taskInstance);
    }

    public String getValorVariavelSemDefinicao(Processo processo, String nome) {
        ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(processo.getIdJbpm());
        Object variable = processInstance.getContextInstance().getVariable(nome);
        return variable != null ? formatarValor(variable) : null;
    }

    private VariavelProcesso getPrimeiraVariavelProcessoAncestral(Processo processo, DefinicaoVariavelProcesso definicao, TaskInstance taskInstance) {
    	VariavelProcesso variavelProcesso = null;
        Processo corrente = processo;
        while (corrente != null && (variavelProcesso == null || Strings.isNullOrEmpty(variavelProcesso.getValor()))) {
            variavelProcesso = getVariavelProcesso(corrente, definicao, taskInstance);
            // Só deve olhar na taskInstance na primeira iteração
            taskInstance = null;
            corrente = corrente.getProcessoPai();
        }
        return variavelProcesso;
    }

    private VariavelProcesso getVariavelProcesso(Processo processo, DefinicaoVariavelProcesso definicao, TaskInstance taskInstance) {
        VariavelProcesso variavelProcesso = inicializaVariavelProcesso(definicao);
        String valorPadrao = definicao.getValorPadrao();
        if (valorPadrao != null) {
            variavelProcesso.setValor(resolveVariavelByValorPadrao(taskInstance, processo, valorPadrao));
        } else {
            String nomeVariavelAsEL = "#{" + definicao.getNome() + "}";
            variavelProcesso.setValor(resolveVariavelByEL(taskInstance, processo, nomeVariavelAsEL));
        }
        return variavelProcesso;
    }

    private String resolveVariavelByValorPadrao(TaskInstance taskInstance, Processo processo, String valorPadrao) {
        String resolved = resolveVariavelByEL(taskInstance, processo, valorPadrao);
        if (resolved != null) return resolved;
        try {
            Object evaluated = resolveByExpression(valorPadrao, processo);
            if (evaluated != null) {
                return formatarValor(evaluated);
            }
        } catch (Exception e) {
            // Não obteve valor avaliando como methodExpression
        }
        return null;
    }

    private String resolveVariavelByEL(TaskInstance taskInstance, Processo processo, String el) {
        Object evaluated = null;
        try {
            if (taskInstance != null) {
                ExecutionContext executionContext = new ExecutionContext(taskInstance.getToken());
                executionContext.setTaskInstance(taskInstance);
                evaluated = JbpmExpressionEvaluator.evaluate(el, executionContext);
                if (evaluated != null) {
                    return formatarValor(evaluated);
                }
            } else if (processo != null) {
                ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(processo.getIdJbpm());
                ExecutionContext executionContext = new ExecutionContext(processInstance.getRootToken());
                evaluated = JbpmExpressionEvaluator.evaluate(el, executionContext);
                if (evaluated != null) {
                    return formatarValor(evaluated);
                }
            }
        } catch (Exception e) {
            // Não obteve valor avaliando el
        }
        return null;
    }

    private String formatarValor(Object variable) {
    	if(variable == null)
    		return "";
    	if (variable instanceof Date) {
    		return new SimpleDateFormat("dd/MM/yyyy").format(variable);
    	} else if (variable instanceof Boolean) {
    		return (Boolean) variable ? "Sim" : "Não";
    	}
		return variable.toString();
	}

	public List<VariavelProcesso> getVariaveisHierquiaProcesso(Integer idProcesso) {
        List<VariavelProcesso> variaveis = new ArrayList<>();
        Processo processo = processoManager.find(idProcesso);
        List<DefinicaoVariavelProcesso> definicaoVariavelList = definicaoVariavelProcessoSearch
                .listVariaveisByFluxo(processo.getNaturezaCategoriaFluxo().getFluxo());
        
        for (DefinicaoVariavelProcesso definicao : definicaoVariavelList) {
            VariavelProcesso variavel = getPrimeiraVariavelProcessoAncestral(processo, definicao, null);
            if (variavel != null) {
                variaveis.add(variavel);
            }
        }
        return variaveis;
    }
    
    /**
     * Adiciona valor baseado em expressão, de valor padrão configurada na definição do fluxo, ao container de variável
     * de processo
     * 
     * @param valorPadrao
     * @param processo
     * @param variavelProcesso
     * @return
     */
    private Object resolveByExpression(String valorPadrao, Processo processo) {
        Object value = null;
        Expressions expressions = Expressions.instance();
        try {
            ValueExpression<Object> valueExpression = expressions.createValueExpression(valorPadrao);
            value = valueExpression.getValue();
        } catch (Exception e) {
        	MethodExpression<Object> methodExpression = expressions.createMethodExpression(valorPadrao, Object.class, Processo.class);
            value = methodExpression.invoke(processo);
        }
        return value;
    }

    private VariavelProcesso inicializaVariavelProcesso(DefinicaoVariavelProcesso definicao) {
        VariavelProcesso variavelProcesso = new VariavelProcesso();
        variavelProcesso.setLabel(definicao.getLabel());
        variavelProcesso.setNome(definicao.getNome());
        return variavelProcesso;
    }

    public UsuarioLogin getUsuarioCadastro(Processo processo) {
        return processo.getUsuarioCadastro();
    }

    public PrioridadeProcesso getPrioridadeProcesso(Processo processo) {
        return processo.getPrioridadeProcesso();
    }
}
