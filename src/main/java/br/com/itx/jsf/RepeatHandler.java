/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.jsf;

import java.io.IOException;
import java.util.List;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public final class RepeatHandler extends TagHandler {

    private final TagAttribute value;
    private final TagAttribute var;
    
    public RepeatHandler(TagConfig config) {
        super(config);
        this.value = this.getAttribute("value");
        this.var = this.getAttribute("var");
    }

    @SuppressWarnings("unchecked")
	public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException {
                
        ValueExpression srcVE = value.getValueExpression(ctx, Object.class);
        List<Object> list = (List<Object>) srcVE.getValue(ctx);
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