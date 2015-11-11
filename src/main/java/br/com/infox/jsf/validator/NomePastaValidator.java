package br.com.infox.jsf.validator;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.seam.util.ComponentUtil;

@FacesValidator("nomePastaValidator")
public class NomePastaValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String nome = null;
        Integer idProcesso = null;
        Integer idPasta = null;
        Map<String, Object> attributes = component.getAttributes();

        nome = (String) attributes.get("submittedValue");
        idProcesso = (Integer) attributes.get("idProcesso");
        idPasta = (Integer) attributes.get("idPasta");

        if (idProcesso == null) {
            throw new ValidatorException(new FacesMessage("Erro ao validar nome da pasta"));
        }

        ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
        Processo processo = processoManager.find(idProcesso);
        for (Pasta pasta : processo.getPastaList()) {
            if (idPasta != null && pasta.getId().equals(idPasta))
                continue;
            if (pasta.getNome().equals(nome)) {
                throw new ValidatorException(new FacesMessage("Já existe pasta com este nome"));
            }
        }
    }
}