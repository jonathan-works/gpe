package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.DefinicaoVariavelProcessoDAO;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Name(DefinicaoVariavelProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DefinicaoVariavelProcessoManager extends Manager<DefinicaoVariavelProcessoDAO, DefinicaoVariavelProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "definicaoVariavelProcessoManager";
    public static final String JBPM_VARIABLE_TYPE = "processo";

    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo) {
        return getDao().listVariaveisByFluxo(fluxo);
    }

    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo,
            int start, int count) {
        return getDao().listVariaveisByFluxo(fluxo, start, count);
    }

    public Long getTotalVariaveisByFluxo(Fluxo fluxo) {
        return getDao().getTotalVariaveisByFluxo(fluxo);
    }

    public DefinicaoVariavelProcesso getDefinicao(Fluxo fluxo, String nome) {
        return getDao().getDefinicao(fluxo, nome);
    }

    public String getNomeAmigavel(DefinicaoVariavelProcesso variavelProcesso) {
        if (variavelProcesso == null || variavelProcesso.getNome() == null) {
            return null;
        }
        return variavelProcesso.getNome();
    }

    public void setNome(DefinicaoVariavelProcesso variavelProcesso,
            String nomeAmigavel) {
        String nome = nomeAmigavel.replace(' ', '_').replace('/', '_');
        variavelProcesso.setNome(nome);
    }
}
