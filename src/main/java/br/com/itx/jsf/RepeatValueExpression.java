/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.jsf;

import java.util.List;

import javax.el.ELContext;
import javax.el.ValueExpression;

/**
 * @author Geraldo Moraes
 * Based on com.sun.facelets.tag.jstl.core.IndexedValueExpression
 */
public final class RepeatValueExpression extends ValueExpression {

    private static final long serialVersionUID = 1L;

    private ValueExpression orig;
    private List<Object> list;
    private int i;

    public RepeatValueExpression(ValueExpression orig, List<Object> list, int i) {
    	this.orig = orig;
        this.list = list;
        this.i = i;
    }

    public Object getValue(ELContext context) {
       	return list.get(i);
    }

    public void setValue(ELContext context, Object value) {
       	list.set(i, value);
    }

    public Class<?> getType(ELContext context) {
       	return list.get(i).getClass();
    }

    public boolean isReadOnly(ELContext context) {
        return false;
    }

    public Class<?> getExpectedType() {
        return Object.class;
    }

    public String getExpressionString() {
        return orig.getExpressionString();
    }

    public boolean equals(Object obj) {
        return orig.equals(obj);
    }

    public int hashCode() {
        return orig.hashCode();
    }

    public boolean isLiteralText() {
        return false;
    }

}