/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da InformaÃ§Ã£o Ltda.

 Este programa Ã© software livre; vocÃª pode redistribuÃ­-lo e/ou modificÃ¡-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versÃ£o 2 da LicenÃ§a.
 Este programa Ã© distribuÃ­do na expectativa de que seja Ãºtil, porÃ©m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implÃ­cita de COMERCIABILIDADE OU 
 ADEQUAÃÃO A UMA FINALIDADE ESPECÃFICA.
 
 Consulte a GNU GPL para mais detalhes.
 VocÃª deve ter recebido uma cÃ³pia da GNU GPL junto com este programa; se nÃ£o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;


@org.jboss.seam.annotations.faces.Validator(id="emailValidator")
@Name("emailValidator")
@BypassInterceptors
public class EmailValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
	    //TODO implementar, o antigo dava erro
		throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Validador de email não implementado", null));
	}
}