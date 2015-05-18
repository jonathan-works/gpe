package br.com.infox.jsf.validator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.seam.util.ComponentUtil;

@org.jboss.seam.annotations.faces.Validator(id = OrdemModeloPastaValidator.NAME)
@Name(OrdemModeloPastaValidator.NAME)
@BypassInterceptors
public class OrdemModeloPastaValidator implements Validator {

    public static final String NAME = "ordemModeloPastaValidator";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Integer ordem = null;
        Integer idFluxo = null;
        Integer idModeloPasta = null;
        Map<String, Object> attrs = component.getAttributes();

        ordem = Integer.parseInt((String) attrs.get("submittedValue"));
        idFluxo = (Integer) attrs.get("idFluxo");
        idModeloPasta = (Integer) attrs.get("idModeloPasta");
        
        if (idFluxo == null) {
            throw new ValidatorException(new FacesMessage("Erro ao validar número de ordem"));
        }
        
        FluxoManager fluxoManager = ComponentUtil.getComponent(FluxoManager.NAME);
        Fluxo fluxo = fluxoManager.find(idFluxo);
        for (ModeloPasta modeloPasta : fluxo.getModeloPastaList()) {
            if (idModeloPasta != null && modeloPasta.getId().equals(idModeloPasta)) continue;
            if (modeloPasta.getOrdem() != null && modeloPasta.getOrdem().equals(ordem)) {
                throw new ValidatorException(new FacesMessage("Já existe Modelo de Pasta com este número de ordem"));
            }
        }
    }
}