package br.com.infox.jsf.validator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.StringUtil;
import br.com.infox.jsf.converter.EditorCleanerConverter;

@FacesValidator("classificacaoEditorValidator")
public class ClassificacaoEditorValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Map<String, Object> attributes = component.getAttributes();
        Map<String, String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
        
        String editorId = (String) attributes.get("editorId");
        
        if ( editorId == null ) {
            throw new ValidatorException(new FacesMessage("Attribute 'editorId' required"));
        }
        
        String editorValue = requestParameterMap.get(editorId);
        editorValue = EditorCleanerConverter.replaceAll(editorValue);
        String valueClassificacao = requestParameterMap.get(component.getClientId());
        
        if ( !StringUtil.isEmpty(editorValue) && valueClassificacao.contains("noSelectionValue")) {
            throw new ValidatorException(new FacesMessage(InfoxMessages.getInstance().get("beanValidation.notNull")));
        }
    }

}
