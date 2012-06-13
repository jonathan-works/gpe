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