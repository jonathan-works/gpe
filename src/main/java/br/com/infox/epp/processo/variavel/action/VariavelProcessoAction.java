package br.com.infox.epp.processo.variavel.action;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.security.Identity;

import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.epp.system.Parametros;

@Name(VariavelProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
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
            this.variaveis = variavelProcessoService.getVariaveis(processo, 
            		DefinicaoVariavelProcessoRecursos.MOVIMENTAR.getIdentificador(), Identity.instance().hasRole(Parametros.PAPEL_USUARIO_EXTERNO.getValue()));
        }
        return this.variaveis;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }
}
