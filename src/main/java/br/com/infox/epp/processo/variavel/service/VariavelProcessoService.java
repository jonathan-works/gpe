package br.com.infox.epp.processo.variavel.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.MethodExpression;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;

@Stateless
@Name(VariavelProcessoService.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
@Transactional
public class VariavelProcessoService {

    public static final String NAME = "variavelProcessoService";

    @In
    private MetadadoProcessoManager metadadoProcessoManager;
    @In
    private ProcessoManager processoManager;
    @In
    private ProcessoTarefaManager processoTarefaManager;

    public List<VariavelProcesso> getVariaveis(Processo processo) {
        List<VariavelProcesso> variaveis = new ArrayList<>();
        DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager = BeanManager.INSTANCE.getReference(DefinicaoVariavelProcessoManager.class);
        List<DefinicaoVariavelProcesso> definicaoVariavelList = definicaoVariavelProcessoManager
                .listVariaveisByFluxo(processo.getNaturezaCategoriaFluxo().getFluxo());
        
        for (DefinicaoVariavelProcesso definicao : definicaoVariavelList) {
            if (definicao.getVisivel()) {
            	variaveis.add(getPrimeiraVariavelProcessoAncestral(processo, definicao, null));
            }
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
    	DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager = BeanManager.INSTANCE.getReference(DefinicaoVariavelProcessoManager.class);
        DefinicaoVariavelProcesso definicao = definicaoVariavelProcessoManager.getDefinicao(processo.getNaturezaCategoriaFluxo().getFluxo(), nome);
        TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstance(idTaskInstance);
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
        while (corrente != null && (variavelProcesso == null || variavelProcesso.getValor() == null || variavelProcesso.getValor().isEmpty())) {
            variavelProcesso = getVariavelProcesso(corrente, definicao, taskInstance);
            // Só deve olhar na taskInstance na primeira iteração
            taskInstance = null;
            corrente = corrente.getProcessoPai();
        }
        return variavelProcesso;
    }

    private VariavelProcesso getVariavelProcesso(Processo processo, DefinicaoVariavelProcesso definicao, TaskInstance taskInstance) {
        Long idJbpm = processo.getIdJbpm();
        if (idJbpm != null) {
            ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(idJbpm);
            Object variable;
            if (taskInstance != null) {
            	// Aqui já pega do processInstance caso não tenha na taskInstance por causa da hierarquia de VariableContainer do jBPM
            	variable = taskInstance.getVariable(definicao.getNome());
            } else {
            	variable = processInstance.getContextInstance().getVariable(definicao.getNome());
            }
            VariavelProcesso variavelProcesso = inicializaVariavelProcesso(definicao);
            if (variable != null) {
                variavelProcesso.setValor(formatarValor(variable));
            } else {
                List<MetadadoProcesso> metadados = metadadoProcessoManager.getMetadadoProcessoByType(processo,
                        definicao.getNome());
                if (metadados != null && metadados.size() > 0) {
                    setValor(processo, metadados, variavelProcesso);
                } else {
                    final String valorPadrao = definicao.getValorPadrao();
                    if (valorPadrao != null) {
                        setValor(valorPadrao, processo, variavelProcesso);
                    } else {
                        variavelProcesso = null;
                    }
                }
            }
            return variavelProcesso;
        }
        return null;
    }

    private String formatarValor(Object variable) {
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
        DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager = BeanManager.INSTANCE.getReference(DefinicaoVariavelProcessoManager.class);
        List<DefinicaoVariavelProcesso> definicaoVariavelList = definicaoVariavelProcessoManager
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
    private void setValor(String valorPadrao, Processo processo, VariavelProcesso variavelProcesso) {
        Object value = null;
        Expressions expressions = Expressions.instance();
        try {
            ValueExpression<Object> valueExpression = expressions.createValueExpression(valorPadrao);
            value = valueExpression.getValue();
        } catch (Exception e) {
            MethodExpression<Object> methodExpression = expressions.createMethodExpression(valorPadrao, Object.class, Processo.class);
            value = methodExpression.invoke(processo);
        }
        variavelProcesso.setValor(value != null ? value.toString() : null);
    }

    /**
     * Adiciona valor baseado em metadado de processo ao container de variável de processo
     * 
     * @param processo
     * @param variavelProcesso
     * @param metadados
     * @return
     */
    private void setValor(Processo processo, List<MetadadoProcesso> metadados, VariavelProcesso variavelProcesso) {
        StringBuilder sb = new StringBuilder();
        boolean firstValue = true;
        for (MetadadoProcesso metadadoProcesso : metadados) {
            if (!firstValue) {
                sb.append(", ");
            }
            sb.append(formatarValor(metadadoProcesso.getValue()));
            firstValue = false;
        }
        variavelProcesso.setValor(sb.toString());
    }

    private VariavelProcesso inicializaVariavelProcesso(DefinicaoVariavelProcesso definicao) {
        VariavelProcesso variavelProcesso = new VariavelProcesso();
        variavelProcesso.setLabel(definicao.getLabel());
        variavelProcesso.setNome(definicao.getNome());
        return variavelProcesso;
    }
}
