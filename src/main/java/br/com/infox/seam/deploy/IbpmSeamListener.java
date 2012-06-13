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
package br.com.infox.seam.deploy;

import javax.servlet.ServletContextEvent;

import org.jboss.seam.servlet.SeamListener;
import org.jboss.seam.util.Conversions;

import br.com.itx.component.FormField;
import br.com.itx.component.Template;
import br.com.itx.component.grid.GridColumn;
import br.com.itx.component.grid.SearchField;
import br.com.itx.converter.FormFieldConverter;
import br.com.itx.converter.GridColumnConverter;
import br.com.itx.converter.SearchFieldConverter;
import br.com.itx.converter.TemplateConverter;

public class IbpmSeamListener extends SeamListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		Conversions.putConverter(GridColumn.class, new GridColumnConverter());
		Conversions.putConverter(FormField.class, new FormFieldConverter());
		Conversions.putConverter(SearchField.class, new SearchFieldConverter());
		Conversions.putConverter(Template.class, new TemplateConverter());
		super.contextInitialized(event);
	}
	
}