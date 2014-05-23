package br.com.infox.ibpm.process.definition.expressionWizard;

import static java.text.MessageFormat.format;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import org.jboss.el.parser.AstCompositeExpression;
import org.jboss.el.parser.ELParser;
import org.jboss.el.parser.ParseException;

public class ExpressionTokenizer {
    private final String expression;
    
    public ExpressionTokenizer(String expression) {
        this.expression=expression;
    }
    
    public String getNodeJSON() throws ParseException,Exception{
        final StringReader reader = new StringReader(expression);
        final ELParser parser = new ELParser(reader);
        final AstCompositeExpression compositeExpression = parser.CompositeExpression();
        final WriteExpressionVisitor visitor = new WriteExpressionVisitor();
        compositeExpression.accept(visitor);
        //return new ExpressionReader(visitor.getStack()).getResultExpression();
        return visitor.toString();
    }
    
    public static String fromNodeJSON(String json){
        final String result = json.substring(1, json.length()-1);
        String[] split = result.split(",");
        Stack<String> stack=new Stack<>();
        for(int i=0,l=split.length;i<l;i++){
            if (!"".equals(split[i])) {
                stack.push(split[i].replaceAll("\"(.+)\"", "$1"));
            }
        }
        return new ExpressionReader(stack).getResultExpression();
    }

    public static void main(String[] args) {
        final ArrayList<String> list = new ArrayList<>();
        list.add("(((((true || false) && (!bool1 || (-0.05 == 0))) || (((0 != 0) || (0 >= 0)) && ((0 > 0) || (0 <= 0)))) && ((((0 < 0) || (( bool1 ? ( bool2 ? ( bool3 ? ( bool4 ? str1 : 'string' ) : ( bool5 ? 0 + 'constante' + str2 : true + 'constante' + 0 ) ) : ( bool6 ? ( bool7 ? str3 + 'constante' + false : 0 + 'constante' + str4 ) : ( bool8 ? true + 'constante' + 0 : 'str' + 'constante' + true ) ) ) : ( bool9 ? ( bool10 ? ( bool11 ? 0 + 'constante' + 'tsa1' : false + 'constante' + 1 ) : ( bool1 ? 'ttttsd' + 'constante' + true : 1 + 'constante' + 0 ) ) : ( bool1 ? ( bool1 ? 0 + 'constante' + 0 : 0 + 'constante' + 0 ) : ( bool1 ? 0 + 'constante' + 0 : 0 + 'constante' + 0 ) ) ) ) == 'Teste')) && ('Teste' != 'Teste')) || false)) && true) ? valor1 : false");
        list.add("'teste'");
        list.add("true");
        list.add("false");
        list.add("( ( false || ( !bool1 || true ) ) && ( ( ( 0==0 || 0!=0 ) || ( 0>=0 || 0<=0 ) ) || ( ( 0>0 || 0<0 ) ) ) )");
        list.add("(!(bool1 && bool2 || True && False) && (-0.05<=(((valor1+2)*3)/100)-1)) ? str1 : 'novo'");
        try {
            String expr = list.get(4);
            System.out.println(expr);
            String resultExpr = new ExpressionTokenizer(format("#'{'{0}'}'", expr)).getNodeJSON();
            System.out.println(resultExpr);
            System.out.println(fromNodeJSON(resultExpr));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
