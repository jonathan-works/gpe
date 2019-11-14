package br.com.infox.epp.processo.variavel.action;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Optional;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;

@Named
@ViewScoped
public class VariavelProcessoAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private Optional<List<VariavelProcesso>> variaveis;
    private Processo processo;

    @Inject
    private VariavelProcessoService variavelProcessoService;
    @Inject
    private PapelManager papelManager;
    
    public void init(Processo processo) {
        this.processo = processo;
    }
    
    public Boolean possuiVariaveis() {
        return getVariaveis() != null && !getVariaveis().isEmpty();
    }

    public List<VariavelProcesso> getVariaveis() {
        if ( variaveis == null && getProcesso() != null ) {
            List<VariavelProcesso> variavelList = variavelProcessoService.getVariaveis(getProcesso(), 
                    DefinicaoVariavelProcessoRecursos.MOVIMENTAR.getIdentificador(), papelManager.isUsuarioExterno(Authenticator.getPapelAtual().getIdentificador()));
            this.variaveis = Optional.<List<VariavelProcesso>>of(variavelList);
        }
        return variaveis.get();
    }
    
    private Processo getProcesso() {
        return processo;
    }

}
