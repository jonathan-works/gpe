package br.com.infox.jsf.validator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.seam.util.ComponentUtil;

@org.jboss.seam.annotations.faces.Validator(id = OrdemPastaValidator.NAME)
@Name(OrdemPastaValidator.NAME)
@BypassInterceptors
public class OrdemPastaValidator implements Validator {
    static final String NAME = "ordemPastaValidator";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Integer ordem = null;
        Integer idProcesso = null;
        Integer idPasta = null;
        Map<String, Object> attributes = component.getAttributes();
        
        ordem = Integer.parseInt((String) attributes.get("submittedValue"));
        idProcesso = (Integer) attributes.get("idProcesso");
        idPasta = (Integer) attributes.get("idPasta");
        
        if (idProcesso == null) {
            throw new ValidatorException(new FacesMessage("Erro ao validar ordem da pasta"));
        }
        
        ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
        Processo processo = processoManager.find(idProcesso);
        for (Pasta pasta : processo.getPastaList()) {
            if (idPasta != null && pasta.getId().equals(idPasta)) continue;
            if (ordem.equals(pasta.getOrdem().intValue())) {
                throw new ValidatorException(new FacesMessage("Já existe pasta com este número de ordem"));
            }
        }
    }
}