package br.com.infox.epp.fluxo.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.dao.DefinicaoVariavelProcessoDAO;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DefinicaoVariavelProcessoManager {

    public static final String JBPM_VARIABLE_TYPE = "processo";
    
    @Inject
    private DefinicaoVariavelProcessoDAO definicaoVariavelProcessoDAO;

    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo) {
        return definicaoVariavelProcessoDAO.listVariaveisByFluxo(fluxo);
    }

    public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo,
            int start, int count) {
        return definicaoVariavelProcessoDAO.listVariaveisByFluxo(fluxo, start, count);
    }

    public Long getTotalVariaveisByFluxo(Fluxo fluxo) {
        return definicaoVariavelProcessoDAO.getTotalVariaveisByFluxo(fluxo);
    }

    public DefinicaoVariavelProcesso getDefinicao(Fluxo fluxo, String nome) {
        return definicaoVariavelProcessoDAO.getDefinicao(fluxo, nome);
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
    	return definicaoVariavelProcessoDAO.getDefinicaoVariavelProcessoListByIdProcesso(idProcesso);
    }
    
    public List<DefinicaoVariavelProcesso> getDefinicaoVariavelProcessoVisivelPainel(Integer idFluxo) {
        return definicaoVariavelProcessoDAO.getDefinicaoVariavelProcessoVisivelPainel(idFluxo);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void moveUp(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	String hql = "update DefinicaoVariavelProcesso o set o.ordem = o.ordem + 1 "
    			+ "where o.fluxo = :fluxo and o.ordem = :ordem";
    	try {
	    	definicaoVariavelProcessoDAO.getEntityManager().createQuery(hql).setParameter("fluxo", definicaoVariavelProcesso.getFluxo())
	    		.setParameter("ordem", definicaoVariavelProcesso.getOrdem() - 1).executeUpdate();
    	} catch (Exception e) {
    		throw new DAOException(e);
    	}
    	definicaoVariavelProcesso.setOrdem(definicaoVariavelProcesso.getOrdem() - 1);
    	update(definicaoVariavelProcesso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void moveDown(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	String hql = "update DefinicaoVariavelProcesso o set o.ordem = o.ordem - 1 "
    			+ "where o.fluxo = :fluxo and o.ordem = :ordem";
    	try {
	    	definicaoVariavelProcessoDAO.getEntityManager().createQuery(hql).setParameter("fluxo", definicaoVariavelProcesso.getFluxo())
	    		.setParameter("ordem", definicaoVariavelProcesso.getOrdem() + 1).executeUpdate();
    	} catch (Exception e) {
    		throw new DAOException(e);
    	}
    	definicaoVariavelProcesso.setOrdem(definicaoVariavelProcesso.getOrdem() + 1);
    	update(definicaoVariavelProcesso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persist(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	definicaoVariavelProcessoDAO.persist(definicaoVariavelProcesso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DefinicaoVariavelProcesso update(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	return definicaoVariavelProcessoDAO.update(definicaoVariavelProcesso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	definicaoVariavelProcessoDAO.remove(definicaoVariavelProcesso);
    }
}
