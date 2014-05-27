package br.com.infox.ibpm.process.definition.expressionWizard;

import java.util.Stack;

import org.jboss.el.parser.AstCompositeExpression;
import org.jboss.el.parser.AstDeferredExpression;
import org.jboss.el.parser.Node;
import org.jboss.el.parser.NodeVisitor;

public final class WriteExpressionVisitor implements NodeVisitor {
    private final Stack<String> stack;
    
    public WriteExpressionVisitor() {
        this.stack = new Stack<>();
    }
    
    @Override
    public void visit(Node node) throws Exception {
        if (node instanceof AstCompositeExpression || node instanceof AstDeferredExpression) {
        } else {
            stack.push(node.toString());
        }
    }
    
    public Stack<String> getStack(){
        return this.stack;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while(stack.size() > 0) {
            sb.append("\"");
            sb.append(stack.pop());
            sb.append("\",");
        }
        sb.append("]");
        return sb.toString();
    }
}