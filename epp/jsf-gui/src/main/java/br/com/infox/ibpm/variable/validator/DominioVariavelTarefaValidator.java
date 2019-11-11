package br.com.infox.ibpm.variable.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("dominioVariavelTarefaValidator")
public class DominioVariavelTarefaValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) return;
        String dominio = (String) value;
        if (!isDominioSqlQuery(dominio)) {
        	validateDominio(dominio);
        }
    }
    
    private boolean isDominioSqlQuery(String value){
    	return value.startsWith("select");
    }
    
    private void validateDominio(String dominio){
    	 Pattern pattern = Pattern.compile("(.+?=.+?;)*");
         Matcher matcher = pattern.matcher(dominio);
         if (!matcher.matches()) {
             throw new ValidatorException(new FacesMessage("Valores inválidos. Deve estar no formato label=valor, separado por ;"));
         }
    }
    
}
