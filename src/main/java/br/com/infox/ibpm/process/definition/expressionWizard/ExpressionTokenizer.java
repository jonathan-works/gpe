package br.com.infox.ibpm.process.definition.expressionWizard;

import static java.text.MessageFormat.format;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import org.jboss.el.parser.AstCompositeExpression;
import org.jboss.el.parser.AstDeferredExpression;
import org.jboss.el.parser.ELParser;
import org.jboss.el.parser.Node;
import org.jboss.el.parser.NodeVisitor;
import org.jboss.el.parser.ParseException;

public class ExpressionTokenizer {

    public ExpressionTokenizer() {
    }

    public void printResultedExpression(String expression) {
        final StringReader reader = new StringReader(expression);
        final ELParser parser = new ELParser(reader);
        //final NodeJSONBuilder visitor = new NodeJSONBuilder();
//        try {
//            final AstCompositeExpression compositeExpression = parser
//                    .CompositeExpression();
//            visitor.buildJSON(compositeExpression);
//            System.out.println(visitor.toString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {
        // String expression =
        // "(((((true || false) && (!bool1 || (-0.05 == 0))) || (((0 != 0) || (0 >= 0)) && ((0 > 0) || (0 <= 0)))) && ((((0 < 0) || (( bool1 ? ( bool2 ? ( bool3 ? ( bool4 ? str1 : 'string' ) : ( bool5 ? 0 + 'constante' + str2 : true + 'constante' + 0 ) ) : ( bool6 ? ( bool7 ? str3 + 'constante' + false : 0 + 'constante' + str4 ) : ( bool8 ? true + 'constante' + 0 : 'str' + 'constante' + true ) ) ) : ( bool9 ? ( bool10 ? ( bool11 ? 0 + 'constante' + 'tsa1' : false + 'constante' + 1 ) : ( bool1 ? 'ttttsd' + 'constante' + true : 1 + 'constante' + 0 ) ) : ( bool1 ? ( bool1 ? 0 + 'constante' + 0 : 0 + 'constante' + 0 ) : ( bool1 ? 0 + 'constante' + 0 : 0 + 'constante' + 0 ) ) ) ) == 'Teste')) && ('Teste' != 'Teste')) || false)) && true) ? ((valor1 + 10) - (valor2 * 15)) : ((valor3 / 20) + (valor4 - valor1))+'teste'";
        final ArrayList<String> list = new ArrayList<>();
        list.add("(((((true || false) && (!bool1 || (-0.05 == 0))) || (((0 != 0) || (0 >= 0)) && ((0 > 0) || (0 <= 0)))) && ((((0 < 0) || (( bool1 ? ( bool2 ? ( bool3 ? ( bool4 ? str1 : 'string' ) : ( bool5 ? 0 + 'constante' + str2 : true + 'constante' + 0 ) ) : ( bool6 ? ( bool7 ? str3 + 'constante' + false : 0 + 'constante' + str4 ) : ( bool8 ? true + 'constante' + 0 : 'str' + 'constante' + true ) ) ) : ( bool9 ? ( bool10 ? ( bool11 ? 0 + 'constante' + 'tsa1' : false + 'constante' + 1 ) : ( bool1 ? 'ttttsd' + 'constante' + true : 1 + 'constante' + 0 ) ) : ( bool1 ? ( bool1 ? 0 + 'constante' + 0 : 0 + 'constante' + 0 ) : ( bool1 ? 0 + 'constante' + 0 : 0 + 'constante' + 0 ) ) ) ) == 'Teste')) && ('Teste' != 'Teste')) || false)) && true) ? valor1 : false");
        list.add("'teste'");
        list.add("true");
        list.add("false");
        list.add("0");
        new NodeVisitor() {
            @Override
            public void visit(Node node) throws Exception {
                System.out.println(node.toString());
            }
        };
        //final NodeJSONBuilder visitor = new NodeJSONBuilder();
        try {
            final StringReader reader = new StringReader(
                    format("#'{'{0}'}'",
                            "(bool1 || ((90+0.5)*-10) >= valor1) ? ('ttttsd' + 'constante' + true) : (1 + 'constante' + 0)"));
            final ELParser parser = new ELParser(reader);
            final AstCompositeExpression compositeExpression = parser
                    .CompositeExpression();
            // visitor.buildJSON(compositeExpression);
            // System.out.println(visitor.toString());
            final Stack<String> stack = new Stack<>();
            compositeExpression.accept(new NodeVisitor() {
                @Override
                public void visit(Node node) throws Exception {
                    if (node instanceof AstCompositeExpression || node instanceof AstDeferredExpression) {
                    } else {
                        stack.push(node.toString());
                    }
                }
            });
            System.out.print("[");
            while(stack.size() > 0) {
                System.out.print("\"");
                System.out.print(stack.pop());
                System.out.print("\",");
            }
            System.out.print("]");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // final ExpressionTokenizer expressionTokenizer = new
        // ExpressionTokenizer();
        // for (String expression : list) {
        // expressionTokenizer.printResultedExpression(format("#'{'{0}'}'",
        // expression));
        // }
    }

}
