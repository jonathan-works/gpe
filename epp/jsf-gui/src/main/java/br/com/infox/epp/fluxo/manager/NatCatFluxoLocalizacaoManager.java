package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.fluxo.dao.NatCatFluxoLocalizacaoDAO;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

/**
 * Classe com as regras de negócio para persistencia, vinculação e alteração dos
 * dados na entidade NatCatFluxoLocalizacao.
 * 
 * @author Daniel
 * 
 */
@Name(NatCatFluxoLocalizacaoManager.NAME)
@AutoCreate
public class NatCatFluxoLocalizacaoManager extends Manager<NatCatFluxoLocalizacaoDAO, NatCatFluxoLocalizacao> {

    private static final long serialVersionUID = -9025640498790727799L;

    public static final String NAME = "natCatFluxoLocalizacaoManager";

    /**
     * Persiste o registro e para cada localizacao filha a localizacao contida
     * no parametro <code>natCatFluxoLocalizacao</code> também é inseirdo um
     * registro com o mesmo registro de naturezaCategoriaFluxo.
     * 
     * @param natCatFluxoLocalizacao
     * @throws DAOException
     */
    public void persistWithChildren(
            NatCatFluxoLocalizacao natCatFluxoLocalizacao) throws DAOException {
        persistChildren(natCatFluxoLocalizacao.getLocalizacao(), natCatFluxoLocalizacao);
    }

    /**
     * Método recursivo para inserir todas as localizações filhas.
     * 
     * @param localizacao
     * @param natCatFluxoLocalizacao
     * @throws DAOException
     */
    private void persistChildren(Localizacao localizacao,
            NatCatFluxoLocalizacao natCatFluxoLocalizacao) throws DAOException {
        if (localizacao.getLocalizacaoList() != null
                && localizacao.getLocalizacaoList().size() > 0) {
            for (Localizacao l : localizacao.getLocalizacaoList()) {
                persistChildren(l, natCatFluxoLocalizacao);
                if (getDao().existsNatCatFluxoLocalizacao(natCatFluxoLocalizacao.getNaturezaCategoriaFluxo(), l)) {
                    continue;
                }
                NatCatFluxoLocalizacao ncfl = new NatCatFluxoLocalizacao();
                ncfl.setLocalizacao(l);
                ncfl.setNaturezaCategoriaFluxo(natCatFluxoLocalizacao.getNaturezaCategoriaFluxo());
                ncfl.setHeranca(natCatFluxoLocalizacao.getHeranca());
                persist(ncfl);
            }
        }
    }

    /**
     * Salva as alterações feitas e replica quando necessário para os filhos,
     * também corrigi a árvore hierarquica quando é modificado apenas a
     * localização ou a NaturezaCategoriaFluxo de um registro onde a heranca =
     * true.
     * 
     * @param natCatFluxoLocalizacao
     * @throws DAOException
     */
    public void saveWithChidren(NatCatFluxoLocalizacao natCatFluxoLocalizacao,
            NatCatFluxoLocalizacao dataBaseOldObject) throws DAOException {
        /*
         * Verifica se a heranca nunca foi ativada
         */
        if (!dataBaseOldObject.getHeranca()
                && !natCatFluxoLocalizacao.getHeranca()) {
            update(natCatFluxoLocalizacao);
            /*
             * Verifica se ativaram a heranca
             */
        } else if (!dataBaseOldObject.getHeranca()
                && natCatFluxoLocalizacao.getHeranca()) {
            persistChildren(natCatFluxoLocalizacao.getLocalizacao(), natCatFluxoLocalizacao);
            update(natCatFluxoLocalizacao);
            /*
             * Verifica se desativaram a heranca
             */
        } else if (dataBaseOldObject.getHeranca()
                && !natCatFluxoLocalizacao.getHeranca()) {
            removeChildren(natCatFluxoLocalizacao.getLocalizacao(), natCatFluxoLocalizacao);
            update(natCatFluxoLocalizacao);
            /*
             * Verifica se a heranca era e continua ativa
             */
        } else {
            /*
             * Verifica se foi modificado o valor da localização, se sim é
             * necessário modificar toda a árvore, do antigo para o novo.
             */
            if (dataBaseOldObject.getLocalizacao().getIdLocalizacao() != natCatFluxoLocalizacao.getLocalizacao().getIdLocalizacao()) {
                removeChildren(dataBaseOldObject.getLocalizacao(), dataBaseOldObject);
                update(natCatFluxoLocalizacao);
                persistChildren(natCatFluxoLocalizacao.getLocalizacao(), natCatFluxoLocalizacao);
                /*
                 * Verifica se foi modificada a relação com
                 * NaturezaCategoriaFluxo caso sim, será necessário atualizar
                 * toda a árvore.
                 */
            } else if (dataBaseOldObject.getNaturezaCategoriaFluxo().getIdNaturezaCategoriaFluxo() != natCatFluxoLocalizacao.getNaturezaCategoriaFluxo().getIdNaturezaCategoriaFluxo()) {
                updateChildren(dataBaseOldObject.getLocalizacao(), dataBaseOldObject.getNaturezaCategoriaFluxo(), natCatFluxoLocalizacao.getNaturezaCategoriaFluxo());
            }
        }
    }

    /**
     * Atualiza todas as NaturezaCategoriaFluxo contidas na heranca.
     * 
     * @param localizacao para buscar a árvore
     * @param oldNCF naturezaCategoriaFluxo antiga
     * @param newNCF naturezaCategoriaFluxo nova a ser atualizada.
     * @throws DAOException
     */
    private void updateChildren(Localizacao localizacao,
            NaturezaCategoriaFluxo oldNCF, NaturezaCategoriaFluxo newNCF) throws DAOException {
        if (localizacao.getLocalizacaoList() != null
                && localizacao.getLocalizacaoList().size() > 0) {
            for (Localizacao l : localizacao.getLocalizacaoList()) {
                updateChildren(l, oldNCF, newNCF);
                NatCatFluxoLocalizacao needUpdate = getDao().getByNatCatFluxoAndLocalizacao(oldNCF, l);
                needUpdate.setNaturezaCategoriaFluxo(newNCF);
                update(needUpdate);
            }
        }
    }

    /**
     * Remove todas as vinculações das localizações filhas da
     * naturezaCategoriaFluxo vinculada.
     * 
     * @param localizacao
     * @param ncfl
     * @throws DAOException 
     */
    private void removeChildren(Localizacao localizacao,
            NatCatFluxoLocalizacao ncfl) throws DAOException {
        if (localizacao.getLocalizacaoList() != null
                && localizacao.getLocalizacaoList().size() > 0) {
            for (Localizacao l : localizacao.getLocalizacaoList()) {
                removeChildren(l, ncfl);
                getDao().deleteByNatCatFluxoAndLocalizacao(ncfl.getNaturezaCategoriaFluxo(), l);
            }
        }
    }

    public List<NatCatFluxoLocalizacao> listByNaturezaCategoriaFluxo(NaturezaCategoriaFluxo ncf) {
        return getDao().listByNaturezaCategoriaFluxo(ncf);
    }

    public boolean existsNatCatFluxoLocalizacao(NaturezaCategoriaFluxo ncf, Localizacao localizacao) {
        return getDao().existsNatCatFluxoLocalizacao(ncf, localizacao);
    }

}
