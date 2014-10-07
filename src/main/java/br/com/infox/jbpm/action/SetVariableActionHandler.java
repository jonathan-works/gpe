package br.com.infox.jbpm.action;

import org.jboss.seam.core.Expressions;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
/**
 * ActionHandler responsável por definir valores
 * às variáveis no contexto jbpm
 * @author erikliberal
 *
 */
public class SetVariableActionHandler implements ActionHandler {

    private static final long serialVersionUID = 1L;
    private String nome;
    private String valor;

    @Override
    public void execute(ExecutionContext executionContext) throws Exception {
        Object val;
        if (valor.startsWith("#{")) {
            val = Expressions.instance().createValueExpression(valor);
        } else {
            val = valor;
        }
        executionContext.getContextInstance().createVariable(nome, val, executionContext.getToken());
    }

    public String getNomeVariavel() {
        return nome;
    }

    public void setNomeVariavel(String nomeVariavel) {
        this.nome = nomeVariavel;
    }

    public String getValorVariavel() {
        return valor;
    }

    public void setValorVariavel(String valorVariavel) {
        this.valor = valorVariavel;
    }

}
