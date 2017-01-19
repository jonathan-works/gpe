package br.com.infox.epp.documento.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.fluxo.definicao.ProcessBuilder;
import br.com.infox.jbpm.action.ActionTemplate;

@Name(ModeloDocumentoAction.NAME)
@Scope(ScopeType.SESSION)
@Startup
public class ModeloDocumentoAction extends ActionTemplate {

    public static final String NAME = "modeloDocumento";
    private static final long serialVersionUID = 1L;

    private ModeloDocumento modeloJbpm;

    public ModeloDocumento getModeloJbpm() {
        return modeloJbpm;
    }

    public void setModeloJbpm(ModeloDocumento modeloJbpm) {
        this.modeloJbpm = modeloJbpm;
    }

    @Override
    public String getExpression() {
        return "modeloDocumento.set";
    }

    @Override
    public String getFileName() {
        return "setModeloDocumento.xhtml";
    }

    @Override
    public String getLabel() {
        return "Atribuir modelo a uma variável";
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public void extractParameters(String expression) {
        if (expression == null || "".equals(expression)) {
            return;
        }
        setParameters(getExpressionParameters(expression));
        if (getParameters().length > 0) {
            ProcessBuilder.instance().getTaskFitter().getCurrentTask().setCurrentVariable((String) getParameters()[0]);
        }
    }

    public void set(String variavel, int... idModeloDocumento) {
        String variavelModelo = variavel + "Modelo";
        StringBuilder s = new StringBuilder();
        for (int i : idModeloDocumento) {
            if (s.length() != 0) {
                s.append(",");
            }
            s.append(i);
        }
        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        Object valor = executionContext.getVariable(variavelModelo);
        if (valor == null) {
            executionContext.getContextInstance().createVariable(variavelModelo, s.toString());
        } else {
            executionContext.getContextInstance().setVariable(variavelModelo, valor + "," + s.toString());
        }
    }

}
