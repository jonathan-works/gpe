package br.com.infox.ibpm.process.definition.expressionWizard;

import static java.text.MessageFormat.format;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExpressionReader {
    private final Stack<String> stack;
    
    public ExpressionReader(final Stack<String> stack){
        this.stack = new Stack<>();
        while(stack.size()>0){
            this.stack.add(stack.pop());
        }
    }
    private static final Pattern trueFalsePattern=Pattern.compile("^(True)|(False)$");
    private static final Pattern valuePattern = Pattern.compile("^Identifier\\[(.*)\\]|String\\[('.*')\\]|Integer\\[(\\d+)\\]|FloatingPoint\\[([\\d]+([.][\\d]+)?)\\]$");
    private static final Pattern operationPattern = Pattern.compile("^Plus|Minus|Mult|Div|And|Or|Equal|NotEqual|GreaterThan|GreaterThanEqual|LessThan|LessThanEqual$");
    private static final Pattern negatPattern = Pattern.compile("^Negative$");
    private static final Pattern notPattern=Pattern.compile("^Not$");
    private static final Pattern exprPattern = Pattern.compile("^Choice$");
    
    private String resolveOperationValue(String oper){
        String result="____";
        if ("Plus".equals(oper)) {
            result="+";
        }else if ("Minus".equals(oper)) {
            result="-";
        }else if ("Mult".equals(oper)) {
            result="*";
        }else if ("Div".equals(oper)) {
            result="/";
        }else if ("And".equals(oper)) {
            result="&&";
        }else if ("Or".equals(oper)) {
            result="||";
        }else if ("Equal".equals(oper)) {
            result="==";
        }else if ("NotEqual".equals(oper)) {
            result="!=";
        }else if ("GreaterThan".equals(oper)) {
            result=">";
        }else if ("GreaterThanEqual".equals(oper)) {
            result=">=";
        }else if ("LessThan".equals(oper)) {
            result="<";
        }else if ("LessThanEqual".equals(oper)) {
            result="<=";
        }
        return result;
    }
    
    public String getResultExpression() {
        final Stack<String> cache=new Stack<>();
        while(stack.size()>0){
            final String current = stack.pop();
            Matcher match=valuePattern.matcher(current);
            
            if (match.find()) {
                cache.push(match.replaceAll("$1$2$3$4"));
            }else if (trueFalsePattern.matcher(current).find()) {
                cache.push(current.toLowerCase());
            }else if (negatPattern.matcher(current).find()){
                cache.push(format("-{0}", cache.pop()));
            }else if (notPattern.matcher(current).find()){
                cache.push(format("!{0}", cache.pop()));
            }else if (operationPattern.matcher(current).find()){
                cache.push(format("({0} {2} {1})", cache.pop(),cache.pop(),resolveOperationValue(current)));
            }else if (exprPattern.matcher(current).find()){
                cache.push(format("({0} ? {1} : {2})", cache.pop(),cache.pop(),cache.pop()));
            }
        }
        return format("#'{'{0}'}'", cache.pop());
    }
}
