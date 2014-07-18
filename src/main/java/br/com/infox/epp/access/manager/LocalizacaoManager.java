package br.com.infox.epp.access.manager;

import java.util.Collection;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.exception.RecursiveException;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.RecursiveManager;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
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
    
    @Override
    public Localizacao persist(Localizacao o) throws DAOException {
        try {
            RecursiveManager.refactor(o);
        } catch (RecursiveException e) {
            throw new DAOException(e);
        }
        return super.persist(o);
    }
    
    @Override
    public Localizacao update(Localizacao o) throws DAOException {
        try {
            RecursiveManager.refactor(o);
        } catch (RecursiveException e) {
            throw new DAOException(e);
        }
        o = super.update(o);
        updateChildren(o);
        return o;
    }

    private void updateChildren(Localizacao o) throws DAOException {
        for (Localizacao loc : o.getLocalizacaoList()) {
            loc.setAtivo(o.getAtivo());
            super.update(loc);
            updateChildren(loc);
        }
    }
}
