package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
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
    
    public List<DefinicaoVariavelProcesso> listVariaveisByIdProcesso(Integer idProcesso) {
    	return getDao().getDefinicaoVariavelProcessoListByIdProcesso(idProcesso);
    }
    
    public List<DefinicaoVariavelProcesso> getDefinicaoVariavelProcessoVisivelPainel(Integer idProcesso) {
        return getDao().getDefinicaoVariavelProcessoVisivelPainel(idProcesso);
    }
    
    public void moveUp(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	String hql = "update DefinicaoVariavelProcesso o set o.ordem = o.ordem + 1 "
    			+ "where o.fluxo = :fluxo and o.ordem = :ordem";
    	try {
	    	getDao().getEntityManager().createQuery(hql).setParameter("fluxo", definicaoVariavelProcesso.getFluxo())
	    		.setParameter("ordem", definicaoVariavelProcesso.getOrdem() - 1).executeUpdate();
    	} catch (Exception e) {
    		throw new DAOException(e);
    	}
    	definicaoVariavelProcesso.setOrdem(definicaoVariavelProcesso.getOrdem() - 1);
    	update(definicaoVariavelProcesso);
    }
    
    public void moveDown(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	String hql = "update DefinicaoVariavelProcesso o set o.ordem = o.ordem - 1 "
    			+ "where o.fluxo = :fluxo and o.ordem = :ordem";
    	try {
	    	getDao().getEntityManager().createQuery(hql).setParameter("fluxo", definicaoVariavelProcesso.getFluxo())
	    		.setParameter("ordem", definicaoVariavelProcesso.getOrdem() + 1).executeUpdate();
    	} catch (Exception e) {
    		throw new DAOException(e);
    	}
    	definicaoVariavelProcesso.setOrdem(definicaoVariavelProcesso.getOrdem() + 1);
    	update(definicaoVariavelProcesso);
    }
}
