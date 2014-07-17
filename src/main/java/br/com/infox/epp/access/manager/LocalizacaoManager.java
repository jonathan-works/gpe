package br.com.infox.epp.access.manager;

import java.util.Collection;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoManager.NAME)
@AutoCreate
public class LocalizacaoManager extends Manager<LocalizacaoDAO, Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoManager";

    public List<Localizacao> getLocalizacoes(final Collection<Integer> ids) {
        return getDao().getLocalizacoes(ids);
    }

    public boolean isLocalizacaoAncestor(final Localizacao ancestor,
            final Localizacao localizacao) {
        return getDao().isLocalizacaoAncestor(ancestor, localizacao);
    }
    
    public String formatCaminhoCompleto(Localizacao localizacao) {
        StringBuilder sb = new StringBuilder(localizacao.getCaminhoCompleto());
        if (sb.charAt(sb.length() -1) == '|') {
            sb.deleteCharAt(sb.length() - 1);
        }
        int index = sb.indexOf("|", 0);
        while (index != -1) {
            sb.replace(index, index + 1, " / ");
            index = sb.indexOf("|", index);
        }
        if (localizacao.getEstruturaFilho() != null) {
            sb.append(": ");
            sb.append(localizacao.getEstruturaFilho().getNome());
        }
        return sb.toString();
    }

    public void atualizarEstruturaPai(Estrutura novaEstruturaPai, Localizacao localizacao) throws DAOException {
        if (getDao().existeLocalizacaoFilhaComEstruturaPaiDiferente(novaEstruturaPai, localizacao)) {
            throw new DAOException("#{messages['localizacao.existeLocalizacaoFilhaComEstruturaPaiDiferente']}");
        }
        if (getDao().existeLocalizacaoFilhaComEstruturaFilho(localizacao)) {
            throw new DAOException("#{messages['localizacao.existeLocalizacaoFilhaComEstruturaFilho']}");
        }
        getDao().atualizarEstruturaPai(novaEstruturaPai, localizacao);
        refresh(localizacao);
        localizacao.setLocalizacaoRaizEstrutura(true);
        update(localizacao);
    }

    public void removerEstruturaPai(Localizacao localizacao) throws DAOException {
        getDao().removerEstruturaPai(localizacao);
    }
    
    @Override
    public Localizacao persist(Localizacao o) throws DAOException {
        validarEstruturaPaiLocalizacaoSuperior(o);
        if (o.getLocalizacaoPai() != null && o.getLocalizacaoPai().getEstruturaPai() != null) {
            o.setEstruturaPai(o.getLocalizacaoPai().getEstruturaPai());
        }
        return super.persist(o);
    }

    private void validarEstruturaPaiLocalizacaoSuperior(Localizacao o) throws DAOException {
        if (o.getEstruturaFilho() != null && (o.getEstruturaPai() != null || (o.getLocalizacaoPai() != null && o.getLocalizacaoPai().getEstruturaPai() != null))) {
            throw new DAOException("#{messages['localizacao.localizacaoSuperiorPossuiEstruturaPai']}");
        }
    }
    
    @Override
    public Localizacao update(Localizacao o) throws DAOException {
        validarEstruturaPaiLocalizacaoSuperior(o);
        if (o.getLocalizacaoPai() != null && o.getLocalizacaoPai().getEstruturaPai() != null) {
            o.setEstruturaPai(o.getLocalizacaoPai().getEstruturaPai());
        }
        return super.update(o);
    }
}
