package br.com.infox.epp.processo.variavel.action;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;

@Name(VariavelProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class VariavelProcessoAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "variavelProcessoAction";

    private Boolean possuiVariaveis;
    private List<VariavelProcesso> variaveis;
    private Processo processo;

    @In
    private VariavelProcessoService variavelProcessoService;

    public Boolean possuiVariaveis() {
        if (this.possuiVariaveis == null) {
            this.possuiVariaveis = !getVariaveis().isEmpty();
        }
        return this.possuiVariaveis;
    }

    public List<VariavelProcesso> getVariaveis() {
        if (this.variaveis == null) {
            this.variaveis = variavelProcessoService.getVariaveis(processo);
        }
        return this.variaveis;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public void save() {
        for (VariavelProcesso variavel : getVariaveis()) {
            variavelProcessoService.save(variavel);
        }
        FacesMessages.instance().add("#{eppmessages['VariavelProcesso_updated']}");
    }
}
