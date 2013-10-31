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

import java.util.regex.Matcher;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;


@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

	private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
	private static final String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
	private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

	private java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
			"^" + ATOM + "+(\\." + ATOM + "+)*@"
					+ DOMAIN
					+ "|"
					+ IP_DOMAIN
					+ ")$",
			java.util.regex.Pattern.CASE_INSENSITIVE
	);
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		if (value != null && !value.toString().isEmpty()) {
			Matcher matcher = pattern.matcher(value.toString());
			if (!matcher.matches()) {
				throw new ValidatorException(new FacesMessage("Email inválido"));
			}
		}
	}
}