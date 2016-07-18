package br.com.infox.jsf.validator;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@FacesValidator("nomePastaValidator")
public class NomePastaValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Map<String, Object> attributes = component.getAttributes();
        
        String nome = (String) value;
        Integer idProcesso = (Integer) attributes.get("idProcesso");
        Integer idPasta = (Integer) attributes.get("idPasta");

        if (idProcesso == null) {
            throw new ValidatorException(new FacesMessage("Erro ao validar nome da pasta"));
        }
        
        Processo processo = getProcessoManager().find(idProcesso);
        List<Pasta> pastaList = getPastaManager().getByProcesso(processo);
        for (Pasta pasta : pastaList) {
            if (idPasta != null && pasta.getId().equals(idPasta))
                continue;
            if (pasta.getNome().equals(nome)) {
                throw new ValidatorException(new FacesMessage("Já existe pasta com este nome"));
            }
        }
    }
    
    protected ProcessoManager getProcessoManager() {
        return BeanManager.INSTANCE.getReference(ProcessoManager.class);
    }

    protected PastaManager getPastaManager() {
        return BeanManager.INSTANCE.getReference(PastaManager.class);
    }
}