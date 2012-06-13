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
package br.com.infox.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@org.jboss.seam.annotations.faces.Validator(id = "cnpjValidator")
@Name("cnpjValidator")
@BypassInterceptors
public class CnpjValidator implements Validator {

	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		try {		
			String cnpjValue = (String) value;
			cnpjValue = cnpjValue.replaceAll("\\.", "");
			cnpjValue = cnpjValue.replaceAll("-", "");
			cnpjValue = cnpjValue.replaceAll("_", "");
			cnpjValue = cnpjValue.replaceAll("/", "");
			Pattern p = Pattern.compile("^[0-9]{14}$");
			Matcher m = p.matcher(cnpjValue);
			if ( !m.matches() || !validarCnpj(cnpjValue) ) {
				throw new ValidatorException(new FacesMessage("CNPJ inv�lido."));
			}
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage("CNPJ inv�lido."), e);
		}
	}

	private boolean validarCnpj(String cnpj) {
		int soma = 0;
		for (int i = 0; i < 4; i++) {
			soma = soma + parseInt(cnpj.charAt(i)) * (5 - i);
		}
		for (int i = 4; i < 12; i++) {
			soma = soma + parseInt(cnpj.charAt(i)) * (13 - i);
		}
		int dv1 = 11 - (soma % 11);
		if (dv1 >= 10) {
			dv1 = 0;
		}
		if (dv1 != parseInt(cnpj.charAt(12))) {
			return false;
		}
		soma = 0;
		for (int i = 0; i < 5; i++) {
			soma = soma + parseInt(cnpj.charAt(i)) * (6 - i);
		}
		for (int i = 5; i < 12; i++) {
			soma = soma + parseInt(cnpj.charAt(i)) * (14 - i);
		}
		soma = soma + dv1 * 2;
		int dv2 = 11 - (soma % 11);
		if (dv2 >= 10) {
			dv2 = 0;
		}
		if (dv2 != parseInt(cnpj.charAt(13))) {
			return false;
		}
		return true;
	}

	private int parseInt(Character c) {
		return Integer.parseInt(c.toString());
	}

}