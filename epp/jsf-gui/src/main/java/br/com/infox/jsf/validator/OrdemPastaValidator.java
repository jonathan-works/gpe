package br.com.infox.jsf.validator;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.seam.util.ComponentUtil;

@FacesValidator("ordemPastaValidator")
public class OrdemPastaValidator implements Validator {

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
        PastaManager pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
        List<Pasta> pastasProcesso = pastaManager.getByProcesso(processo);
        for (Pasta pasta : pastasProcesso) {
            if (idPasta != null && pasta.getId().equals(idPasta)) continue;
            if (pasta.getOrdem()!= null && pasta.getOrdem().equals(ordem)) {
                throw new ValidatorException(new FacesMessage("Já existe pasta com este número de ordem"));
            }
        }
    }
}