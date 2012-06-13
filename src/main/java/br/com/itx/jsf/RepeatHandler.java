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

import java.io.IOException;
import java.util.List;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public final class RepeatHandler extends TagHandler {

    private final TagAttribute value;
    private final TagAttribute var;
    
    public RepeatHandler(TagConfig config) {
        super(config);
        this.value = this.getAttribute("value");
        this.var = this.getAttribute("var");
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
                
        ValueExpression srcVE = value.getValueExpression(ctx, Object.class);
        List list = (List)srcVE.getValue(ctx);
        if (list == null) {
        	return;
        }
        String v = var.getValue(ctx);
        VariableMapper vars = ctx.getVariableMapper();
        ValueExpression old = vars.setVariable(v, null);
        try {
        	for (int i = 0; i < list.size(); i++) {
                vars.setVariable(v, new RepeatValueExpression(srcVE, list, i));
                nextHandler.apply(ctx, parent);
            }
        } finally {
            vars.setVariable(v, old);
        }
    }
    
}