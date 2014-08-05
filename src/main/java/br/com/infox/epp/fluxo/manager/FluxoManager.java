package br.com.infox.epp.fluxo.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.RaiaPerfil;

/**
 * Classe Manager para a entidade Fluxo
 * 
 * @author tassio
 */
@Name(FluxoManager.NAME)
@AutoCreate
public class FluxoManager extends Manager<FluxoDAO, Fluxo> {

    private static final long serialVersionUID = -6521661616139554331L;

    public static final String NAME = "fluxoManager";

    @In
    private RaiaPerfilManager raiaPerfilManager;
    
    /**
     * Retorna todos os Fluxos ativos
     * 
     * @return lista de fluxos ativos
     */
    public List<Fluxo> getFluxoList() {
        return getDao().getFluxoList();
    }

    public boolean contemProcessoAtrasado(final Fluxo fluxo) {
        return getDao().quantidadeProcessosAtrasados(fluxo) > 0;
    }

    public Fluxo getFluxoByDescricao(final String descricao) {
        return getDao().getFluxoByDescricao(descricao);
    }

    public boolean existemProcessosAssociadosAFluxo(final Fluxo fluxo) {
        return getDao().getQuantidadeDeProcessoAssociadosAFluxo(fluxo) > 0;
    }

    public boolean existeFluxoComDescricao(final String descricao) {
        return getDao().existeFluxoComDescricao(descricao);
    }

    public boolean existeFluxoComCodigo(final String codigo) {
        return getDao().existeFluxoComCodigo(codigo);
    }

    public Collection<Integer> getIdsLocalizacoesRaias(final Fluxo fluxo) {
        Set<Integer> idsLocalizacao = new HashSet<Integer>();
        List<RaiaPerfil> listByFluxo = raiaPerfilManager.listByFluxo(fluxo);
        for (RaiaPerfil raiaPerfil : listByFluxo) {
            idsLocalizacao.add(raiaPerfil.getPerfilTemplate().getLocalizacao().getIdLocalizacao());
        }
        return idsLocalizacao;
    }
}
