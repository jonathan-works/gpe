package br.com.infox.epp.access.manager;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.exception.RecursiveException;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.RecursiveManager;
import br.com.infox.epp.access.dao.LocalizacaoDAO;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.type.TipoUsoLocalizacaoEnum;
import br.com.infox.epp.fluxo.manager.RaiaPerfilManager;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;

@Name(LocalizacaoManager.NAME)
@AutoCreate
@Stateless
public class LocalizacaoManager extends Manager<LocalizacaoDAO, Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoManager";
    
    @In private RaiaPerfilManager raiaPerfilManager;
    @In private UnidadeDecisoraMonocraticaManager unidadeDecisoraMonocraticaManager;

    public List<Localizacao> getLocalizacoes(final Collection<Integer> ids) {
        if (ids.isEmpty()) {
            ids.add(-1);
        }
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
            if (!o.getAtivo()) {
                validarInativacao(o);
            }
            RecursiveManager.refactor(o);
        } catch (RecursiveException e) {
            refresh(o);
            throw new DAOException(e);
        }
        o = super.update(o);
        refresh(o);
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

    public void inativar(Localizacao localizacao) throws DAOException {
        validarInativacao(localizacao);
        localizacao.setAtivo(false);
        update(localizacao);
    }
    
    public void validarInativacao(Localizacao localizacao) throws DAOException {
        boolean invalid = false;
        String msg = "Não foi possível inativar, pois existe {0} que utiliza esta localização ou suas filhas.";
        List<TipoUsoLocalizacaoEnum> usos = getDao().getUsosLocalizacao(localizacao);
        if (usos.contains(TipoUsoLocalizacaoEnum.P)) {
            msg = MessageFormat.format(msg, "perfil{0}");
            invalid = true;
        }
        if (usos.contains(TipoUsoLocalizacaoEnum.RP)) {
            if (invalid) {
                msg = MessageFormat.format(msg, ", raia{0}");
            } else {
                msg = MessageFormat.format(msg, "raia{0}");
                invalid = true;
            }
        }
        if (usos.contains(TipoUsoLocalizacaoEnum.UDM)) {
            if (invalid) {
                msg = MessageFormat.format(msg, ", unidade decisora monocrática");
            } else {
                msg = MessageFormat.format(msg, "unidade decisora monocrática");
                invalid = true;
            }
        }
        if (usos.contains(TipoUsoLocalizacaoEnum.UDC)) {
            if (invalid) {
                msg = MessageFormat.format(msg, ", unidade decisora colegiada");
            } else {
                msg = MessageFormat.format(msg, "unidade decisora colegiada");
                invalid = true;
            }
        }
        
        if (invalid) {
            msg = msg.replace("{0}", "");
            throw new DAOException(msg);
        }
    }

    public Localizacao getLocalizacaoByEstruturaPaiName(String nomeEstruturaPai) {
        return getDao().getlocalizacaoByNomeEstruturaPai(nomeEstruturaPai);
    }
    
    public Localizacao getLocalizacaoDentroEstrutura(String nomeLocalizacao) {
        return getDao().getLocalizacaoDentroEstrutura(nomeLocalizacao);
    }
    
    public Localizacao getLocalizacaoForaEstruturaByNome(String nomeLocalizacao){
    	return getDao().getLocalizacaoForaEstruturaByNome(nomeLocalizacao);
    }
    
        public Localizacao getLocalizacaoByCodigo(String codigo) {
    	return getDao().getLocalizacaoByCodigo(codigo);
    }

	public Localizacao getLocalizacaoByNome(String nomeLocalizacao) throws DAOException {
		return getDao().getLocalizacaoByNome(nomeLocalizacao); 
	}
	
	public List<Localizacao> getLocalizacoesByEstruturaFilho(Estrutura estruturaFilho) {
		return getDao().getLocalizacoesByEstruturaFilho(estruturaFilho);
	}
    
}