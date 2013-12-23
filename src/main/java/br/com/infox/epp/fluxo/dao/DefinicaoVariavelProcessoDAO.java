package br.com.infox.epp.fluxo.dao;

import static br.com.infox.core.constants.WarningConstants.*;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.DEFINICAO_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.LIST_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.PARAM_FLUXO;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.PARAM_NOME;
import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.TOTAL_BY_FLUXO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Scope(ScopeType.EVENT)
@AutoCreate
@Name(DefinicaoVariavelProcessoDAO.NAME)
public class DefinicaoVariavelProcessoDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "definicaoVariavelProcessoDAO";

    @SuppressWarnings(UNCHECKED)
    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo) {
        return createQueryVariaveisProcessoByFluxo(fluxo).getResultList();
    }

    @SuppressWarnings(UNCHECKED)
    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo, int start, int count) {
        return createQueryVariaveisProcessoByFluxo(fluxo).setFirstResult(start)
                .setMaxResults(count).getResultList();
    }

    public Long getTotalVariaveisByFluxo(Fluxo fluxo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedSingleResult(TOTAL_BY_FLUXO, parameters);
    }

    private Query createQueryVariaveisProcessoByFluxo(Fluxo fluxo) {
        return getEntityManager().createNamedQuery(LIST_BY_FLUXO, DefinicaoVariavelProcesso.class)
                .setParameter(PARAM_FLUXO, fluxo);
    }

    public DefinicaoVariavelProcesso getDefinicao(Fluxo fluxo, String nome) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FLUXO, fluxo);
        parameters.put(PARAM_NOME, nome);
        return getNamedSingleResult(DEFINICAO_BY_FLUXO, parameters);
    }
}
